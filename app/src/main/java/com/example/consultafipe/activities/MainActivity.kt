package com.example.consultafipe.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.consultafipe.R
import com.example.consultafipe.dominio.*
import com.example.consultafipe.services.RetrofitInitializer
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ProgressBar



/* todo
* escolher tipo do veiculoo
* favoritar um veiculo
* lista de favoritos
* historico de preços
* broadcast para agendar a consulta diariamente
* */
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var adapter: ArrayAdapter<Marca>
    private lateinit var adapterModelo: ArrayAdapter<Modelo>
    private lateinit var adapterAno: ArrayAdapter<ModeloAno>
    private lateinit var adapterTipo: ArrayAdapter<String>


    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        progress.visibility = View.VISIBLE
        when(p0?.adapter){
            adapter -> this.carregarModelos()
            adapterModelo -> this.carregarAnos()
            adapterAno -> this.carregarCarro()
            adapterTipo -> this.carregarMarcas()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapterTipo = ArrayAdapter(this,android.R.layout.simple_spinner_item, arrayOf("carros", "motos", "caminhoes"))
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerTipo.adapter = adapterTipo
        spinnerTipo.onItemSelectedListener = this
        adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
        adapterModelo = ArrayAdapter(this,android.R.layout.simple_spinner_item)
        adapterModelo.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerModelo.adapter = adapterModelo
        spinnerModelo.onItemSelectedListener = this
        adapterAno = ArrayAdapter(this,android.R.layout.simple_spinner_item)
        adapterAno.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinnerAno.adapter = adapterAno
        spinnerAno.onItemSelectedListener = this
        progress.visibility = View.VISIBLE
        this.carregarMarcas()
    }

    private fun carregarMarcas(){
        val positionTipo = spinnerTipo.selectedItemPosition
        val nomeTipo = adapterTipo.getItem(positionTipo)!!
        adapter.clear()
        adapterModelo.clear()
        adapterAno.clear()
        val call = RetrofitInitializer().carroService().listarMarcas(nomeTipo)
        call.enqueue(object : Callback<List<Marca>> {
            override fun onResponse(call: Call<List<Marca>?>?, response: Response<List<Marca>?>?) {
                response?.body()?.let {
                    adapter.addAll(it)
                }
            }

            override fun onFailure(call: Call<List<Marca>>, t: Throwable) {
                exibirErro(t)
            }
        })
    }
    private fun carregarModelos(){
        val positionTipo = spinnerTipo.selectedItemPosition
        val nomeTipo = adapterTipo.getItem(positionTipo)!!
        adapterModelo.clear()
        adapterAno.clear()
        val positionMarca = spinner.selectedItemPosition
        val codigoMarca = adapter.getItem(positionMarca)!!.codigo
        val call = RetrofitInitializer().carroService().listarModelos(nomeTipo, codigoMarca)
        call.enqueue(object : Callback<ModelosResposta> {
            override fun onResponse(call: Call<ModelosResposta>?, response: Response<ModelosResposta>?) {
                response?.body()?.let {
                    adapterModelo.addAll(it.modelos)
                }
            }
            override fun onFailure(call: Call<ModelosResposta>, t: Throwable) {
                exibirErro(t)
            }
        })
    }
    private fun carregarAnos(){
        val positionTipo = spinnerTipo.selectedItemPosition
        val nomeTipo = adapterTipo.getItem(positionTipo)!!
        adapterAno.clear()
        val positionMarca = spinner.selectedItemPosition
        val codigoMarca = adapter.getItem(positionMarca)!!.codigo
        val positionModelo = spinnerModelo.selectedItemPosition
        val codigoModelo = adapterModelo.getItem(positionModelo)!!.codigo
        val call = RetrofitInitializer().carroService().listarAnos(nomeTipo, codigoMarca, codigoModelo)
        call.enqueue(object : Callback<List<ModeloAno>> {
            override fun onResponse(call: Call<List<ModeloAno>?>?, response: Response<List<ModeloAno>?>?) {
                response?.body()?.let {
                    adapterAno.addAll(it)
                }
            }
            override fun onFailure(call: Call<List<ModeloAno>>, t: Throwable) {
                exibirErro(t)
            }
        })
    }
    private fun carregarCarro(){
        val positionTipo = spinnerTipo.selectedItemPosition
        val nomeTipo = adapterTipo.getItem(positionTipo)!!
        val positionMarca = spinner.selectedItemPosition
        val codigoMarca = adapter.getItem(positionMarca)!!.codigo
        val positionModelo = spinnerModelo.selectedItemPosition
        val codigoModelo = adapterModelo.getItem(positionModelo)!!.codigo
        val positionAno = spinnerAno.selectedItemPosition
        val codigoAno = adapterAno.getItem(positionAno)!!.codigo
        val call = RetrofitInitializer().carroService().obterCarro(nomeTipo, codigoMarca, codigoModelo, codigoAno)
        call.enqueue(object : Callback<Carro> {
            override fun onResponse(call: Call<Carro>?, response: Response<Carro>?) {
                response?.body()?.let {
                    valorVeiculo.text = "Preço: ${it.Valor}"
                    progress.visibility = View.GONE
                }
            }
            override fun onFailure(call: Call<Carro>, t: Throwable) {
                exibirErro(t)
            }
        })
    }

    private fun exibirErro(t:Throwable){
        Toast.makeText(this, t.message!!, Toast.LENGTH_LONG).show()
    }


}
