package br.edu.unicesumar.produto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

public class ProdutoActivity extends Activity implements View.OnClickListener, Runnable {
    private static final String CATEGORIA = "CONEXAO";
    private ProgressDialog dialog;
    private int idBotaoSelecionado;

    private static final String HOST = "calculadorawagnerfusca.herokuapp.com";

    private static final String PATH_PRODUTO = "/rest/json/produto/";
    private static final String INSERIR_PRODUTO = "post/";
    private static final String LISTAR_PRODUTO = "listas/";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_produto);

        mapearBotoesDaTela();

    }

    private void mapearBotoesDaTela() {
        Button btnListar = (Button) findViewById(R.id.btnListar);
        btnListar.setOnClickListener(this);

        Button btnAdicionar = (Button) findViewById(R.id.btnAdicionar);
        btnAdicionar.setOnClickListener(this);

    }

    public void onClick(View view) {
        dialog = ProgressDialog
                .show(this,
                        "Loading...",
                        "Acessando base de dados com web service, por favor aguarde...",
                        false, true);

        idBotaoSelecionado = view.getId();
        new Thread(this).start();
    }

    @Override
    public void run() {
        EditText textLembrete = (EditText) findViewById(R.id.lembrete);
        final TextView textResultado = (TextView) findViewById(R.id.resultado);

        String novoPath = "";
        String resultado = "";
        try {
            switch (idBotaoSelecionado) {
                /*case R.id.btnListar:
                    novoPath = PATH + LISTAR;
                    resultado = converseGet(HOST, 80, novoPath);
                    apresentaResultadoNoTextView(textResultado, resultado);
                    break;
                case R.id.btnAdicionar:
                    novoPath = PATH + INSERIR + URLEncoder.encode(textLembrete.getText().toString());
                    resultado = converseGet(HOST, 80, novoPath);
                    apresentaResultadoNoTextView(textResultado, resultado);
                    break;*/
                case R.id.btnListar:
                    novoPath = PATH_PRODUTO + LISTAR_PRODUTO;
                    resultado = converseGet(HOST, 80, novoPath);
                    listarJson(resultado);
                    break;
                case R.id.btnAdicionar:
                    novoPath = PATH_PRODUTO + INSERIR_PRODUTO;
                    resultado = conversePostJson(HOST, 80, novoPath, criarJson());
                    apresentaResultadoNoTextView(textResultado, resultado);

                    break;
                default:
                    break;
            }
        } catch (Exception e){
            Log.e(CATEGORIA, e.getMessage(), e);
        } finally{
            dialog.dismiss();
        }

    }

    private void apresentaResultadoNoTextView(TextView textResultado, String resultado) {
        textResultado.setVisibility(View.VISIBLE);
        textResultado.setText("Resultado: " + resultado);
        Log.i(CATEGORIA, String.valueOf(resultado));
    }

    public static String converseGet(String host, int port, String path)
            throws IOException {
        HttpHost target = new HttpHost(host, port);
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(path);
        HttpEntity results = null;

        try {
            HttpResponse response = client.execute(target, get);
            results = response.getEntity();
            return EntityUtils.toString(results);
        } catch (Exception e) {
            Log.e(CATEGORIA, e.getMessage(), e);
            throw new RuntimeException("Nao encontrou o webservice " + e.getMessage());
        } finally {
            if (results != null) {
                try {
                    results.consumeContent();
                } catch (IOException e) {

                }
            }
        }

    }

    public static String conversePostJson(String host, int port, String path, JSONObject json)
            throws IOException {
        HttpHost target = new HttpHost(host, port);
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(path);
        HttpEntity results = null;

        StringEntity se = new StringEntity(json.toString());
        post.setEntity(se);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");

        try {
            HttpResponse response = client.execute(target, post);
            results = response.getEntity();
            return EntityUtils.toString(results);
        } catch (Exception e) {
            Log.e(CATEGORIA, e.getMessage(), e);
            throw new RuntimeException("Nao encontrou o webservice " + e.getMessage());
        } finally {
            if (results != null) {
                try {
                    results.consumeContent();
                } catch (IOException e) {

                }
            }
        }

    }

    private void listarJson(String resultado) throws JSONException {
        JSONObject root = new JSONObject(resultado);
        JSONArray produtos = root.getJSONArray("produtos");
        for (int i = 0; i < produtos.length(); i++) {
            JSONObject c = produtos.getJSONObject(i);
            String nome = c.getString("nome");
            Integer quantidade = c.getInt("quantidade");
            Log.i(CATEGORIA, nome + "-" + quantidade);
        }

    }

    private JSONObject criarJson() {
        try {
            JSONObject produto2 = new JSONObject();
            produto2.put("nome", "Zenphone");
            produto2.put("quantidade", 11);
            Log.i(CATEGORIA, produto2.toString());
            return produto2;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
