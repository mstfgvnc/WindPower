package com.mustafaguvenc.windpower.ui.enterfragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mustafaguvenc.windpower.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_enter.*
import javax.inject.Inject

@AndroidEntryPoint
class EnterFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var selectedLocation : LatLng
    private lateinit var locationManager : LocationManager
    private var selectedAltitude = 0.0

    var gps_enabled = false
    var network_enabled = false
    var provider :String = "no"
    var permission :String = "no"
    var lastLocation : Location? = null
    var lastKnownLatLng : LatLng? = null
    var frictionValue = 0.0
    val frictions = arrayOf(
        "Lütfen Sürtünme Katsayısını Seçiniz...",
        "Düz zemin, göl ya da okyanus (0.10)",
        "Çayır ya da işlenmemiş toprak (0.14)",
        "Kırsal arazi, ayak seviyesinde çayır, seyrek ağaçlı bölge (0.16)",
        "Mahsüllü toprak ve ağaçlı bölge (0.20)",
        "Orman ve binaların olduğu bölge (0.23)",
        "Ormanlık kasaba ya da şehir (0.29)",
        "Büyük binaların olduğu şehirler (0.40)"
    )

    private lateinit var locatonListener : LocationListener

    private val viewModel : EnterFragmentViewModel by viewModels()
    @Inject
    lateinit var adapterTurbine : EnterFragmentAdapter

    var pagerSnapHelper = PagerSnapHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_enter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        viewModel.getTurbines()
        turbinesForRecyclerView.layoutManager=LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        turbinesForRecyclerView.adapter=adapterTurbine
        pagerSnapHelper.attachToRecyclerView(turbinesForRecyclerView)
        indicator.attachToRecyclerView(turbinesForRecyclerView,pagerSnapHelper)
        adapterTurbine.registerAdapterDataObserver(indicator.adapterDataObserver)

        observeLiveData()

        val frictionValues = arrayOf(0.0,0.1,0.14,0.16,0.20,0.23,0.29,0.40)

        val spinnerAdapter=createSpinnerAdapter()
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_tem)
        spinner.adapter=spinnerAdapter
        spinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

               frictionValue=frictionValues.get(p2)
               println(viewModel.turbines.value?.get(0)?.power)
               println(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        calculate.setOnClickListener {

            if(!numberTurbins.text.toString().equals("") && frictionValue!=0.0){
                val turbinPosition=indicator.getSnapPosition(turbinesForRecyclerView.layoutManager)
                val action = EnterFragmentDirections.actionEnterFragmentToResultFragment(
                    selectedLocation.latitude.toString(),
                    selectedLocation.longitude.toString(),
                    turbinPosition,
                    frictionValue.toFloat(),
                    numberTurbins.text.toString().toInt()
                )
                it.findNavController().navigate(action)
            }else{
                Toast.makeText(context,"Lütfen Türbin Sayısını ve Sürtünme Katsayısını Giriniz...",Toast.LENGTH_SHORT).show()
            }

        }


    }

    fun observeLiveData(){
        viewModel.turbines.observe(viewLifecycleOwner,  { turbines ->
            turbines?.let{
                turbinesForRecyclerView.visibility = View.VISIBLE
                adapterTurbine.updateTurbineList(turbines)
            }

        })
        viewModel.elevation.observe(viewLifecycleOwner,  { elevation ->
            elevation?.let{

                selectedAltitude = elevation.data!!.get(0)

            }

        })
        viewModel.windSpeeds.observe(viewLifecycleOwner,{ windSpeeds->
            windSpeeds?.let {

            }

        })

    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        mMap=googleMap
        mMap.setOnMapLongClickListener(myListener)
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex : Exception) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex : Exception) {
        }

        if(gps_enabled){
            provider= LocationManager.GPS_PROVIDER
            permission = Manifest.permission.ACCESS_FINE_LOCATION
        }else if(network_enabled){
            provider= LocationManager.NETWORK_PROVIDER
            permission = Manifest.permission.ACCESS_COARSE_LOCATION
        }

        locatonListener = object : LocationListener{
            override fun onLocationChanged(p0: Location) {

            }

        }


        if(ContextCompat.checkSelfPermission(requireActivity(),permission) != PackageManager.PERMISSION_GRANTED){
            if(viewModel.customPreferences.getLatitude()==0f ){
                lastKnownLatLng = LatLng(41.015137,28.979530)
            }
            else{
                lastKnownLatLng = LatLng(viewModel.customPreferences.getLatitude()!!.toDouble(),viewModel.customPreferences.getLongitude()!!.toDouble())
            }
            selectedLocation = lastKnownLatLng as LatLng
            mMap.addMarker(MarkerOptions().position(lastKnownLatLng).title("Selected Area"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng,12f))

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission),1)

        }else{
            locationManager.requestLocationUpdates(provider,1,1f,locatonListener)
            lastLocation = locationManager.getLastKnownLocation(provider)

            if(lastLocation != null){
                mMap.clear()

                if(viewModel.customPreferences.getLatitude()==0f ){
                    lastKnownLatLng = LatLng(lastLocation!!.latitude ,lastLocation!!.longitude)
                }
                else{
                    lastKnownLatLng = LatLng(viewModel.customPreferences.getLatitude()!!.toDouble(),viewModel.customPreferences.getLongitude()!!.toDouble())
                }

            }else{
                if(viewModel.customPreferences.getLatitude()==0f ){
                    lastKnownLatLng = LatLng(41.015137,28.979530)
                }
                else{
                    lastKnownLatLng = LatLng(viewModel.customPreferences.getLatitude()!!.toDouble(),viewModel.customPreferences.getLongitude()!!.toDouble())
                    Toast.makeText(context,"3", Toast.LENGTH_LONG).show()
                }


            }
            selectedLocation = lastKnownLatLng as LatLng
            mMap.addMarker(MarkerOptions().position(lastKnownLatLng).title("Selected Area"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng,12f))

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.size>0){
                if(ContextCompat.checkSelfPermission(requireContext(),permission)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(provider,1,1f,locatonListener)

                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    val myListener = object  :GoogleMap.OnMapLongClickListener {
        @SuppressLint("MissingPermission")
        override fun onMapLongClick(p0: LatLng) {

            mMap.clear()
            mMap.addMarker(MarkerOptions().position(p0).title("Selected Area"))
            selectedLocation = p0

        }

    }

    fun createSpinnerAdapter():ArrayAdapter<String>{

        val spinnerAdapter = object : ArrayAdapter<String>(requireContext(),R.layout.spinner_tem,frictions){

            override fun isEnabled(position: Int): Boolean {
                return true

            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view=  super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) {
                    tv.setTextColor(Color.GRAY)
                } else {
                    tv.setTextColor(Color.BLACK)
                }
                return view
            }

        }
        return spinnerAdapter
    }

}