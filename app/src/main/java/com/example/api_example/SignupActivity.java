package com.example.api_example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.internal.SignInButtonImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;




public class SignupActivity extends AppCompatActivity implements View.OnClickListener{


    //2つの変数を定義。上の方は、ただのTAGにこのアクティビティ の名前を代入しただけ。Logで使う。したのやつはSigninを行うためののなんかの変数。わかったら記述する。
    private static final String TAG = "SignupActivity";
    private static final int RC_SIGN_IN = 9001;


    //1.FirebaseAuthインスタンスを宣言。2.GoogleSignInClientインスタンスを宣言。
    //3.このクラスで利用するTextViewの名称を宣言している。
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    private TextView mDetailTextView;


    //1,onCreateメソッド。ライフイベントの一番最初の処理。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //1.宣言したTextViewの変数に、対応するViewのidを取得する。
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);

        //ここで、SignupActivityをimplemetsした結果、それぞれのfindViewByIdメソッドに、OnclickListenerを実装しなくてすんだ。
        //設計の観点から、OnClickListenerには3つの方法がある。1.今回のように、Activitiをimplementsする方法。2.無名のクラスにonClickの無名クラスをセットする方法。
        //3.クリック後の処理を個別のクラスとして作成する方法。
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnectButton).setOnClickListener(this);


        //ここで、　GoogleSignInOptionsクラスに対して、引数としてDEFAULT_SIGN_INを指定し、オブジェクトを宣言する。
        //この下に書いてあるのは、それぞれGoogleに対して要求するオプションになっている。
        //requestIdtokenは、「Firebase クライアント アプリがカスタム バックエンド サーバーと通信する場合、
        // そのサーバーに現在ログインしているユーザーを特定する必要が生じる場合があります。
        // これを安全に行うために、正常なログイン後、ユーザーの ID トークンを HTTPS を使ってサーバーに送信します。
        // 次に、サーバー上で ID トークンの完全性と信頼度を確認し、ID トークンの uid を取得します。
        // サーバーで現在ログインしているユーザーを安全に特定するために、この方法で送信された uid を使用できます。」との説明があった。

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //さらに、GoogleSIgnInClientクラスのオブジェクトとして宣言していたmGoogleSIgnInClientに、GoogleSignInオブジェクトを生成し、getClientメソッドを適用する
        // 引数にrequestする内容のgsoを与える。
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //やっているのは、onCreateメソッドにおいて、FirebaseAuthインスタンスを取得し、初期化すること。
        mAuth = FirebaseAuth.getInstance();
    }

    //onCreateの次。
    @Override
    public void onStart(){
        super.onStart();

        //FirebaseUserインスタンスに、FirebaseAuthインスタンスに対して、getCurrentUserメソッドを適用。
        //currentUserには、アカウントデータが入っている。ちょっと覗いてみよう。
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //CurrentUserに入っている情報を確かめるための条件分岐とそのコードを記述。
        if (currentUser != null) {
            String confirm_currentUser = currentUser.toString();
            Log.d(TAG, "confirm_currentUserには右の情報が含まれる=>" + confirm_currentUser);
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            try {
                startActivity(intent);
            }finally {
                Log.d(TAG, "intentが飛ばせていない");
            }

        }else {
            Log.d(TAG, "ログインしていないときにLogcatにて出力されます。currentuserはnullです");
        }

//        //UpdateUIメソッドに対して、currentUserを渡している。updateUIメソッドの処理を詳しく見ていく。
//        updateUI(currentUser);
    }


    //StartActivityから移動してきた。ここでは、StartActivityの結果を取得するためのメソッドになる。
    //Android developersを見ると、アクティビティから結果を取得するという説明がある。なお、引数は3つあり、int,int,Intentとなっている。
    //requestCodeは、StartActivityに渡した要求コード。ここではRC_SIGN_IN。
    //resultCodeは、Result_OKかRESULT_CANCELEDのどちらか。ここでは事前に指定していない。
    //dataは、データが入ったIntent。
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);


        //requestに対するレスポンスが返ってきて、requestCodeが9001の時。
        if(requestCode == RC_SIGN_IN){
            //Task<GoogleSignInAccount>インスタンスを宣言し、これに飛ばしたIntentがdataとして入っている？？
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //Googleログイン成功。
            try {
                //Firebaseで認証する。firebaseAuthWithGoogleに引数accountをつけて呼び出す。
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.d(TAG, "Firebaseにログイン成功");
            //Googleログイン失敗。
            }catch (ApiException e){
                Log.w(TAG,"Google sign in failed", e);
                updateUI(null);
            }
        }
    }


    //Firebaseへの認証をするためのメソッド。これはこういう書き方なのだろう。でスルーする。
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        //AUthCredentialの作成は、GoogeAuthProviderクラスのgetCredentialメソッドを用いて行う。引数には、GoogleSignInAccountインスタンスacctからgetIdTokenを呼び出して、トークンを取得して渡す。
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        //ここで、認証を行う。認証後の処理は、OnCompleteListenerを組み込んで、その中のonCompleteメソッドで行う。
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if (task.isSuccessful()){
                            Log.d(TAG, "signInWithCredential: success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            intent.putExtra("user_info", user.toString());
                            startActivity(intent);
                            Log.d(TAG, "ここでサインインが終了して、intent処理は終了しているよ！");
                        }else{
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "認証失敗", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    //一番処理が楽、Intentインスタンスに、mGoogleSignInClientインスタンスにgetSignInIntentメソッドを適用。現状、mGoogleSignInClientにはGoogleにリクエストする
    //はずの、メールアドレス、Uid等のオプションが入っている。
    //mGoogleSignInClient.getSignInIntentの記述自体はStartActivityForResultを呼ぶためにIntentを受けるだけのメソッド。
    //startActivityForResultでは、引数に(Intent, int)となっている。従って、１行目は単純にIntentインスタンスに代入することで、なんというか、キャストしただけ？な気がする。
    private void signIn(){
        Intent signinIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResultメソッドを確認する。引数に、上のsigninIntentと、このアクティビティで宣言したRC_SIGN_INを入れい￥ているだけ。え？これでsigninって終わりなの？？
        //サインインした結果、表示される画面とかあるのに、そこはスルーでいいの？！
        //確認する。確認した。
        //この処理は単純にIntentを飛ばすことが目的。これで、Googleが提供しているログイン画面を呼び出している。この後は、onActivityResultメソッドへ移る。
        startActivityForResult(signinIntent, RC_SIGN_IN);
    }

    private void signOut(){
        //Firebaseからサインアウト。
        mAuth.signOut();
        //ここでこの後の遷移等の処理を決める。
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task){
                updateUI(null);
            }
        });
    }

    private void revokeAccess(){
        mAuth.signOut();
        //signoutと同様。
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, new OnCompleteListener<Void>(){
           @Override
           public void onComplete(@NonNull Task<Void> task){
               updateUI(null);
           }
        });
    }

    //FirebaUserのインスタンスが引数として与えられている。
    private void updateUI(FirebaseUser user){
        //1.onStartメソッドより、currentUserが引数に与えられている場合。ユーザーがログインしていればログイン情報が、ログインしていなければnullが入っている。
        //ログインしている場合。
        if(user != null){
            //activity_signupにて宣言しているTextViewのidを持っているそれぞれの定数に、setTextする。内容は、Googleから取得したemailとUid。
            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            //ここで、signinbuttonを有しているボタンテキストに、setVIsibilityメソッドを適用し、引数としてView.Goneを与えている。
            //このメソッドは、ボタンタグに対して表示・非表示を与えているだけのメソッド。それぞれ、GONEで非表示、VISIBLEで表示されるようになっている。
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.signOutAndDisconnect).setVisibility(View.VISIBLE);
        //ログインしていない場合。ログインしている場合を見ればほとんど同じなので割愛。
        }else{
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.signOutAndDisconnect).setVisibility(View.GONE);
        }
    }


    //onStartまではまだユーザーには画面が見えていない。この後、表示される画面に対して、タップしたときの処理を振り分ける。
    @Override
    public void onClick(View v){
        //まず、activity_signupでタップされたボタンのidを取得し、iに代入する。
        int i = v.getId();
        //iがsign_in_buttonと一致していれば、sign_inメソッドを呼ぶ。
        if(i == R.id.sign_in_button){
            signIn();
        //iがsign_out_buttonならsignoutメソッドを呼ぶ。
        }else if (i == R.id.sign_out_button){
            signOut();
        //iがdisconnectButtonならrebokeAccessメソッドを呼ぶ。
        }else if (i == R.id.disconnectButton){
            revokeAccess();
        }
    }
}
