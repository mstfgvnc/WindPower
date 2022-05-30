package com.mustafaguvenc.windpower.model

data class WindModel(

    val list: List<Data>
)

data class Data(

    val wind: Wind
)

data class Wind(

    val speed: Double
)
