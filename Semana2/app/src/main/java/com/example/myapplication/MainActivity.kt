package com.example.myapplication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Variables de vistas
    private lateinit var spinnerConversionType: Spinner
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var editTextValue: EditText
    private lateinit var buttonConvert: Button
    private lateinit var textViewResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializamos las vistas
        spinnerConversionType = findViewById(R.id.spinnerConversionType)
        spinnerFrom = findViewById(R.id.spinnerFrom)
        spinnerTo = findViewById(R.id.spinnerTo)
        editTextValue = findViewById(R.id.editTextValue)
        buttonConvert = findViewById(R.id.buttonConvert)
        textViewResult = findViewById(R.id.textViewResult)

        // Configuramos el spinner principal y su listener para actualizar los spinners secundarios
        spinnerConversionType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
            ) {
                when (spinnerConversionType.selectedItem.toString()) {
                    "Temperatura" -> setAdapterForConversion(R.array.temperature_units)
                    "Moneda" -> setAdapterForConversion(R.array.currency_units)
                    "Longitud" -> setAdapterForConversion(R.array.length_units)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // También podemos sincronizar los dos spinners secundarios con el mismo adaptador
        // Ya que ambas listas deben ser iguales (origen y destino)
        // El método setAdapterForConversion se encarga de ello.

        // Botón de conversión
        buttonConvert.setOnClickListener {
            val input = editTextValue.text.toString()
            if (input.isEmpty()) {
                Toast.makeText(this, "Ingresa un valor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val value = input.toDouble()
            val fromUnit = spinnerFrom.selectedItem.toString()
            val toUnit = spinnerTo.selectedItem.toString()
            val resultValue = convertValue(
                type = spinnerConversionType.selectedItem.toString(),
                fromUnit = fromUnit,
                toUnit = toUnit,
                value = value
            )
            val resultText = " $resultValue $toUnit"
            textViewResult.text = resultText
        }

    }

    private fun setAdapterForConversion(arrayId: Int) {
        val adapter = ArrayAdapter.createFromResource(
            this,
            arrayId,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter
    }

    // Función de conversión que maneja tres casos: Temperatura, Moneda y Longitud
    private fun convertValue(type: String, fromUnit: String, toUnit: String, value: Double): Double {
        return when (type) {
            "Temperatura" -> convertTemperature(fromUnit, toUnit, value)
            "Moneda" -> convertCurrency(fromUnit, toUnit, value)
            "Longitud" -> convertLength(fromUnit, toUnit, value)
            else -> value
        }
    }

    // Conversión de temperaturas: Convertimos a Celsius de forma intermedia
    private fun convertTemperature(from: String, to: String, value: Double): Double {
        // Convertir de la unidad de origen a Celsius
        val valueInCelsius = when (from) {
            "Celsius" -> value
            "Fahrenheit" -> (value - 32) * 5/9
            "Kelvin" -> value - 273.15
            else -> value
        }
        // Convertir de Celsius a la unidad destino
        return when (to) {
            "Celsius" -> valueInCelsius
            "Fahrenheit" -> valueInCelsius * 9/5 + 32
            "Kelvin" -> valueInCelsius + 273.15
            else -> valueInCelsius
        }
    }

    // Conversión de monedas: Ejemplo simple, tasas fijas
    // Ejemplo: 1 Peso = 0.05 Dólar, 1 Peso = 0.04 Euro; ajustar según necesidad
    private fun convertCurrency(from: String, to: String, value: Double): Double {
        // Convertir la moneda de origen a Soles (unidad base)
        val valueInSoles = when (from) {
            "Soles" -> value
            "Dólar" -> value * 3.70   // Ejemplo: 1 Dólar = 3.70 Soles
            "Euro" -> value * 4.00    // Ejemplo: 1 Euro = 4.00 Soles
            else -> value
        }

        // Convertir de Soles a la moneda destino
        return when (to) {
            "Soles" -> valueInSoles
            "Dólar" -> valueInSoles / 3.68
            "Euro" -> valueInSoles / 3.86
            else -> valueInSoles
        }
    }

    // Conversión de longitudes: Ejemplo simple
    // Usamos metro como unidad base
    private fun convertLength(from: String, to: String, value: Double): Double {
        // Convertir de la unidad origen a metros
        val valueInMeters = when (from) {
            "Metro" -> value
            "Kilómetro" -> value * 1000
            "Millas" -> value * 1609.34
            else -> value
        }
        // Convertir de metros a la unidad destino
        return when (to) {
            "Metro" -> valueInMeters
            "Kilómetro" -> valueInMeters / 1000
            "Millas" -> valueInMeters / 1609.34
            else -> valueInMeters
        }
    }
}
