package com.mustafaguvenc.windpower.ui.resultfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mustafaguvenc.windpower.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_result.*
import org.apache.mahout.math.jet.stat.Gamma

@AndroidEntryPoint
class ResultFragment : Fragment() {

    private val viewModel : ResultViewModel by viewModels()
    lateinit var latitude : String
    lateinit var longitude : String
    var idTurbin =-1
    var numberOfTurbines = 0
    var friction = 0.0
    var elevation = -1.0
    var startTime = 0
    var endTime = 0
    var week = 0
    private val API_KEY="xxxx"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            latitude=ResultFragmentArgs.fromBundle(it).lat
            longitude=ResultFragmentArgs.fromBundle(it).lon
            idTurbin=ResultFragmentArgs.fromBundle(it).id
            friction=ResultFragmentArgs.fromBundle(it).friction.toDouble()
            numberOfTurbines=ResultFragmentArgs.fromBundle(it).numberOfTurbines

        }

        viewModel.getElevationFromAPI(latitude,longitude)
        viewModel.getTurbineFromSQLite()

        observeLiveData()


    }

    fun observeLiveData(){
        viewModel.elevation.observe(viewLifecycleOwner,{
            elevation=it.data!!.get(0)
            viewModel.getTimeFromAPI()
        })
        viewModel.unixTime.observe(viewLifecycleOwner,{
            println(it)
            startTime=it-31449600+1
            println(startTime)
            endTime=startTime+604799
            println(endTime)

            viewModel.getWindSpeedsFromAPI(latitude,longitude,startTime.toString(),endTime.toString(),API_KEY)
            week++

        })
        viewModel.windModel.observe(viewLifecycleOwner,{
            if(week<52){
                startTime=endTime+1
                endTime=startTime+604799
                week++
                println(week)

                viewModel.getWindSpeedsFromAPI(latitude,longitude,startTime.toString(),endTime.toString(),API_KEY)
            }else{
                week=0
                val updateList = arrayListOf<Double>()
                var total=0.0
                var totalFirst=0.0
                val turbine = viewModel.turbineModel.value?.get(idTurbin)
                println(turbine!!.h.toString())
                println(((turbine!!.h!!+elevation )/ (10.0 + elevation)))
                println(friction)
                for(i in viewModel.windDataList){
                    val x = i * Math.pow(((turbine!!.h!! )/ (10.0 )),friction)
                    updateList.add(x)
                    totalFirst += i
                    total +=  x
                }
                val meanWindSpeed  = totalFirst / updateList.size
                val meanWindSpeedDelayed = total/updateList.size
                val calculateList = arrayListOf<Double>()
                var totalCalculateList= 0.0
                for (i in updateList){
                    val y =Math.pow(i-meanWindSpeedDelayed,2.0)/(updateList.size-1)
                    calculateList.add(y)
                    totalCalculateList += y
                }
                val k = Math.pow(Math.sqrt(totalCalculateList)/meanWindSpeedDelayed , -1.086)
                val c = meanWindSpeedDelayed / Gamma.gamma((k+1)/k)
                val cP = capasityFactor(k,c,turbine!!.cutin!!,turbine.nom!!,turbine.cutout!!)
                val yearlyEnergy = numberOfTurbines * updateList.size * turbine.power!! * cP
                val maintenceCostPerKwh = 0.02
                val maintanceCost = yearlyEnergy * maintenceCostPerKwh
                val initialCostPerKwh = 1300
                val initialCost = turbine.power * initialCostPerKwh * numberOfTurbines
                val energySellingPrice = 0.073
                val annualIncome = (energySellingPrice * yearlyEnergy)-maintanceCost
                val amortizationPeriod = initialCost / annualIncome

                println("k= " + k.toString())
                println("c= " + c.toString())
                println("Cp= " + cP.toString())

                tvLat.text= "Enlem = " +  latitude
                tvLon.text="Boylam = " +  longitude
                tvTurbinModel.text="Türbin Modeli = Türbin - "+ (idTurbin+1).toString()
                tvTurbinPower.text="Türbin Gücü = " + turbine.power + " kW"
                tvTurbinHeight.text="Türbin Yüksekliği  = " + turbine.h + " m"
                tvNumberTurbin.text="Türbin Sayısı = " + numberOfTurbines.toString()
                tvMeanWindSpeed.text = "Ortalama Rüzgar Hızı = " + (Math.floor(meanWindSpeed*100)/100).toString()+" m/s"
                tvMeasuringHeight.text = "Rüzgar Hızı Ölçüm Yükseliği = 10 m"
                tvMeanWindSpeedDelayed.text = "Ötelenmiş Ortalama Rüzgar Hızı = " +  (Math.floor(meanWindSpeedDelayed*100)/100).toString()+" m/s"
                tvInstallationCostPerKwh.text= "Kurulum Maliyeti /  kW = "+initialCostPerKwh.toString()+ " $"
                tvInstallationCost.text="Toplam Kurulum Maliyeti = " + initialCost.toString() + " $ "
                tvKparameter.text = "Şekil Parametresi (k) = " + (Math.floor(k*100)/100).toString()
                tvCparameter.text = "Ölçek Parametresi (c) = " + (Math.floor(c*100)/100).toString()
                tvYearlyEnergy.text="Yıllık Üretilen Enerji = " +(Math.floor(yearlyEnergy*100)/100).toString() + " kWh"
                tvEnergySellingPricePerKwh.text="Enerji Satış Fiyatı / kWh = " + energySellingPrice.toString()+ " $"
                tvAnnualIncome.text="Yıllık Gelir = "+ (Math.floor((energySellingPrice * yearlyEnergy)*100)/100).toString()+ " $"
                tvMaintanceCostPerKwh.text="Bakım Onarım Maliyeti / kWh = "+ maintenceCostPerKwh.toString()+ " $"
                tvAnnualMaintanceCost.text="Yıllık Bakım Onarım Maliyeti = "+ (Math.floor(maintanceCost*100)/100).toString()+ " $"
                tvAnnualProfit.text="Yıllık Kar = " +((Math.floor((energySellingPrice * yearlyEnergy)-maintanceCost)*100)/100).toString()+ " $"
                tvAmortizationPeriod.text="Amortisman Süresi = " + (Math.floor(amortizationPeriod*100)/100).toString()+" Yıl "
                resultLayout.visibility=View.VISIBLE

                viewModel.loading.value=false

            }
        })


        viewModel.loading.observe(viewLifecycleOwner,{
            if(it){
                loading.visibility=View.VISIBLE
                sonuc.visibility=View.GONE
            }else{
                loading.visibility=View.GONE
                sonuc.visibility=View.VISIBLE
            }

        })
    }

    fun capasityFactor (k: Double,c:Double,vIn:Double,vNom:Double,vOut:Double):Double{

        val integralResult = Math.abs((Math.pow(vNom,2.0)* Math.pow(Math.pow((vNom/c),k),-(2/k))*
                Gamma.incompleteGamma((k+2) / k,Math.pow((vNom/c),k))-(Math.pow(vIn,2.0)*
                Math.pow(Math.E,-Math.pow((vNom/c),k))))/ (Math.pow(vIn,2.0) - Math.pow(vNom,2.0)))
                -
                Math.abs((Math.pow(vIn,2.0)* Math.pow(Math.pow((vIn/c),k),-(2/k))*
                        Gamma.incompleteGamma((k+2) / k,Math.pow((vIn/c),k))-(Math.pow(vIn,2.0)*
                        Math.pow(Math.E,-Math.pow((vIn/c),k))))/ (Math.pow(vIn,2.0) - Math.pow(vNom,2.0)))
                +
                Math.abs(-Math.pow(Math.E,-Math.pow((vOut/c),k)))-Math.abs(-Math.pow(Math.E,-Math.pow((vNom/c),k)))

        return integralResult

    }


}