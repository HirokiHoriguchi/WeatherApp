package com.example.api_example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class WeatherInfoActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);

//        intentにgetIntentメソッドを用いて、intentで飛ばされてきたMainActivityからの「cityName」と「cityID」を取得する。
//        そしてこれをString型にキャストする。
        Intent intent = getIntent();
        String CityName = intent.getStringExtra("cityName");
        String CityId = intent.getStringExtra("cityId");


//        TextViewをactivity_weather_infoが三つ持っているので、それらに該当するViewのID番号を取得する。
        TextView tvCityName = (TextView) findViewById(R.id.tvCityName);
        TextView tvWeatherTelop = (TextView) findViewById(R.id.tvWeatherTelop);
        TextView tvWeatherDesc = (TextView) findViewById(R.id.tvWeatherDesc);

//        ここで、新しくreceiverとしてWeatherInfoReceiverのオブジェクトを生成する。引数は、上記にて定義したcityName,tvCityName,tvWeatherTelop,tvWeatherDescの4つ。
        WeatherInfoReceiver receiver = new WeatherInfoReceiver(CityName, tvCityName, tvWeatherTelop, tvWeatherDesc);
//       WeatherInfoReceverのオブジェクトであるreceiverに、executeメソッドを用いて、引数をCityIdに設定する。これで,WeatherInfoReceiverクラスのオブジェクトであるreceiverで、URLレスポンスをさせるためのcityid番号が渡った。
        receiver.execute(CityId);

        imageView = findViewById(R.id.image_view);
        Button buttonFadeIn = findViewById(R.id.show_botton);
        buttonFadeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAnime();
            }
        });




    }

    //        非同期処理を行うためのクラス。AsyncTaskの<>の理解のためにいかに記述する。
//        第一引数は引数、第二引数は経過について、第三引数は戻り値についての型を指定している。従って、それぞれの引数にintとかStringとかvoidがあることもある。
    private class WeatherInfoReceiver extends AsyncTask<String, String, String> {
        private String _cityName;
        private TextView _tvCityName;
        private TextView _tvWeatherTelop;
        private TextView _tvWeatherDesc;


        public WeatherInfoReceiver(String cityName, TextView tvCityName, TextView tvWeatherTelop, TextView tvWeatherDesc) {
            _cityName = cityName;
            _tvCityName = tvCityName;
            _tvWeatherTelop = tvWeatherTelop;
            _tvWeatherDesc = tvWeatherDesc;
        }

        private final String TAG = "doInBackground";

        //        別スレッドで非同期処理が実行され、その時に行う処理内容
        @Override
        public String doInBackground(String... params) {
//            MainAvtivityからgetIntentで取得した都道府県のidがparamsに格納されている。ここで、paramsは可変長変数であり、リストのような振る舞いをすることがわかっている。
            String id = params[0];
//            ここで、APIを取得するためのURLをString型の変数に代入しておく。なお、ここでidが末尾につくことで、各都市に該当するURLにアクセス可能。
            String urlStr = "http://weather.livedoor.com/forecast/webservice/json/v1?city=" + id;

//            ここで、HttpURLConnectionクラスのオブジェクトconを定義し、nullを代入する。
//            同様に、InputStreamクラスのオブジェクトisを宣言し、nullを代入する。
//            結果を代入するresultには、空文字を代入しておく。これに以下の処理ないで何かしらの文字列を代入することになる。
            HttpURLConnection con = null;
            InputStream is = null;
            String result = "";

            try {
//                URLクラス＝World Wide Web上のリソースへのポインタを表すクラス。リソースはファイルやディレクトリのような単純なものよりも、データベースや検索エンジンに対するクエリーなど、複雑なオブジェクトへの参照であることもある。
//                URLの種類や形式によっては、公式リファレンスの以下を参照(https://docs.oracle.com/javase/jp/6/api/java/net/URL.html)。ここでは、そのURLクラスのオブジェクトurlを宣言した。
                URL url = new URL(urlStr);
//                ここで、HTTPConnectionのオブジェクトconに、openConnectionメソッドを使用して取得した何かを代入する。
                con = (HttpURLConnection) url.openConnection();
//                ここで、HTTP通信のGETメソッドを指定している。
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.setInstanceFollowRedirects(false);
                con.setDoOutput(false);
//                ここで、接続を開始する。
                con.connect();
                int responseCode = con.getResponseCode();
                System.out.println("HTTPレスポンスコードを表示します");
                System.out.println(responseCode);
                Log.d(TAG, String.format("responseCode = %d", responseCode));
                switch (responseCode) {
                    case HttpURLConnection.HTTP_OK:
                            //ここで、nullにしておいたisに、conに対してgetInputStreamメソッドを使用したものを代入する。ここが問題だと思われる。InputStreamが機能していない。
                        is = con.getInputStream();
                            //resultに、is2Stringメソッドに引数として、isを与えたものを代入する。この後に、以下の処理でそれぞれの接続等々を切断し、結果としてresultを返す。
                        result = is2String(is);
                        break;
                    default:
                        // responseCodeが200(HTTP OK)でない場合は何かのエラーでうまくいってません。
                        break;
                }


//                ここで、nullにしておいたisに、conに対してgetInputStreamメソッドを使用したものを代入する。
//                is = con.getInputStream();
////                resultに、is2Stringメソッドに引数として、isを与えたものを代入する。この後に、以下の処理でそれぞれの接続等々を切断し、結果としてresultを返す。
//                result = is2String(is);

            } catch (MalformedURLException ex) {
            } catch (IOException ex) {
                System.out.println("情報取得に失敗しました");
                ex.printStackTrace();
                System.out.println(con);
                System.out.println(is);
            } finally {
                if (con != null) {
                    con.disconnect();
                }

                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                    }
                }
            }
            return result;
        }

        //        メインスレッドで実行させる処理(非同期処理で得たAPIの結果などをViweに紐付け、反映させる等)
        @Override
        public void onPostExecute(String result) {
            String desc = "";
            String dateLabel = "";
            String telop = "";
            try {
                JSONObject rootJSON = new JSONObject(result);
                JSONObject descriptionJSON = rootJSON.getJSONObject("description");
                desc = descriptionJSON.getString("text");
                JSONArray forecasts = rootJSON.getJSONArray("forecasts");
                JSONObject forecastNow = forecasts.getJSONObject(0);
                dateLabel = forecastNow.getString("dateLabel");
                telop = forecastNow.getString("telop");
            } catch (JSONException ex) {
            }

            _tvCityName.setText(String.format("%sの%sの天気:", _cityName, dateLabel));
            _tvWeatherTelop.setText(telop);
            _tvWeatherDesc.setText(desc);
        }


        //          ここでは、取得したデータをsbという名前で宣言したStringBifferに代入したものを戻り値として返すメソッド。
        private String is2String(InputStream is) throws IOException, UnsupportedEncodingException {
//            BufferReaderのオブジェクトとして、readerを宣言し、これにBufferReaderにInputStreamを引数として渡してオブジェクトを生成する。
//            そもそも、BufferReaderとは何か？文字ストリームを数文字「まとめて」取得するための文字入力ストリームクラス。
//            通常、read()メソッドを呼び出す毎に1文字単位で取得するが、ネットワーク環境等によっては数文字をまとめて取得した方が処理が早まる可能性がある。
//            そこで、一回のread()メソッドで数文字をあらかじめ取得し、「バッファ」に保存することで、その後の取得をバッファから返し、処理速度の向上を図るのがこのクラスの役目。
//            文字ストリームとは、「文字の並び」を意味する。Stringクラス、char型の配列、テキストファイル 等が文字ストリームとなる。バッファとは緩衝材という意味の文言。なぜバッファがこの文脈にあるかというと、、、？
//            =>元々は、「とりあえずのデータを格納する変数」という意味として、変数名にbufをつけて使用していた。
//            これらの変数に、文字列を格納することが多くなり、それが転じて、現在では「文字列を格納する変数」という意味で使用されることが多くなった。
//            StringBufferクラスの「Buffer」も同じ意味。
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            文字列を連結する処理が多い場合に使用する。StringBufferの方がStringBuilderよりも文字列の連結に欠損が見られないなどの優位性があるみたい。
            StringBuffer sb = new StringBuffer();
            String st = "";
//            ここで、StringBufferクラスにガンガンappendして、charにキャストした数字も含む文字列をどんどん連結させていく。
//            whileのなかで、lineが0未満、つまりもうreadLineメソッドで読み込むことができなくなた場合に、ループを抜ける。
            while ((st = reader.readLine()) != null) {
                sb.append(st);
            }
//            ここで、StringBufferのオブジェクトをString型にキャストして、戻り値として返す。これが99行目に該当する。
            return sb.toString();
        }
    }

    private void setAnime(){
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnimation.setDuration(2000);

        RotateAnimation rotate = new RotateAnimation(0.0f, 120.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.5f);

        rotate.setDuration(2000);

        AnimationSet animationSet = new AnimationSet(true);

        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(rotate);

        imageView.startAnimation(animationSet);
    }
}
