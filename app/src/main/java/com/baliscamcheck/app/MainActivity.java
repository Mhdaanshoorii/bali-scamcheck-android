package com.baliscamcheck.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private static final String SUPABASE_URL = "https://mqfipqpmsuapfctotjzo.supabase.co";
    private static final String SUPABASE_KEY = "sb_publishable_4sFZXYku2HwgJ7wvHWwKLA_tN-nKxk-";

    private LinearLayout root, content;
    private EditText search;
    private TextView resultTitle, resultBody;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    int bg = Color.rgb(7, 18, 15);
    int card = Color.rgb(14, 31, 25);
    int green = Color.rgb(54, 211, 153);
    int white = Color.WHITE;
    int muted = Color.rgb(164, 185, 177);

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        buildUI();
    }

    private void buildUI() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(bg);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(18), dp(18), dp(30));
        scroll.addView(content);

        TextView brand = text("BALI SCAMCHECK", 25, white);
        brand.setTypeface(null, 1);
        content.addView(brand);

        TextView sub = text("CEK DULU SEBELUM PERCAYA", 11, green);
        sub.setTypeface(null, 1);
        content.addView(sub, lp(0, 28, 0, 0));

        LinearLayout hero = box();
        TextView h = text("🛡️  Lindungi transaksi kamu", 20, white);
        h.setTypeface(null, 1);
        hero.addView(h);
        hero.addView(text("Cek nomor, rekening, e-wallet, atau target mencurigakan berdasarkan laporan komunitas.", 13, muted), lp(0, 8, 0, 0));
        content.addView(hero, lp(0, 20, 0, 0));

        TextView label = text("CHECK TARGET", 11, muted);
        label.setTypeface(null, 1);
        content.addView(label, lp(0, 20, 0, 6));

        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);

        search = new EditText(this);
        search.setHint("Nomor / rekening / username");
        search.setHintTextColor(Color.rgb(110,135,125));
        search.setTextColor(white);
        search.setTextSize(14);
        search.setSingleLine(true);
        search.setPadding(dp(14), 0, dp(10), 0);
        GradientDrawable inputBg = rounded(Color.rgb(18,39,32), 14);
        search.setBackground(inputBg);
        searchRow.addView(search, new LinearLayout.LayoutParams(0, dp(52), 1));

        Button check = button("CEK");
        searchRow.addView(check, lp(10, 52, 0, 0));
        content.addView(searchRow);

        LinearLayout quick = new LinearLayout(this);
        quick.setOrientation(LinearLayout.HORIZONTAL);
        quick.setPadding(0, dp(14), 0, 0);
        addQuick(quick, "📞\nNomor", v -> doCheck("phone"));
        addQuick(quick, "🏦\nRekening", v -> doCheck("bank"));
        addQuick(quick, "🔗\nLink", v -> doCheck("link"));
        content.addView(quick);

        resultTitle = text("HASIL PEMERIKSAAN", 11, muted);
        resultTitle.setTypeface(null, 1);
        content.addView(resultTitle, lp(0, 24, 0, 7));

        resultBody = text("Masukkan target lalu tekan CEK.", 14, muted);
        LinearLayout resultCard = box();
        resultCard.addView(resultBody);
        content.addView(resultCard);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.VERTICAL);
        actions.setPadding(0, dp(18), 0, 0);
        action(actions, "🚨  BANTUAN DARURAT", v -> dial("110"));
        action(actions, "📋  LAPORKAN PENIPUAN", v -> openWeb("https://mhdaanshoorii.github.io/bali-scamcheck/"));
        action(actions, "🏛️  VERIFIKASI RESMI", v -> openWeb("https://laporsiberbali.id/"));
        content.addView(actions);

        TextView footer = text("BALI SCAMCHECK  •  Community Safety Network", 10, Color.rgb(105,130,120));
        footer.setGravity(Gravity.CENTER);
        content.addView(footer, lp(0, 28, 0, 0));

        check.setOnClickListener(v -> doCheck("all"));

        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
    }

    private void doCheck(String type) {
        String target = search.getText().toString().trim();
        if (target.length() < 4) {
            search.setError("Masukkan minimal 4 karakter");
            return;
        }
        resultBody.setText("⏳ Memeriksa database komunitas...");
        executor.execute(() -> {
            try {
                String encoded = URLEncoder.encode(target, "UTF-8");
                String url = SUPABASE_URL + "/rest/v1/reports?select=target,target_type,category,area,story,created_at&target=ilike.*" + encoded + "*&status=eq.verified&limit=50";
                String response = request(url);
                JSONArray arr = new JSONArray(response);
                runOnUiThread(() -> showResults(target, arr));
            } catch (Exception e) {
                runOnUiThread(() -> resultBody.setText("⚠️ Tidak dapat terhubung.\nCoba lagi beberapa saat."));
            }
        });
    }

    private void showResults(String target, JSONArray arr) {
        if (arr.length() == 0) {
            resultBody.setText("🟢  BELUM ADA LAPORAN\n\nTidak ditemukan laporan terverifikasi untuk target ini.\n\nTetap waspada sebelum transfer atau membayar DP.");
            return;
        }
        int n = arr.length();
        String level = n >= 4 ? "🔴  RISIKO TINGGI" : n >= 2 ? "🟠  WASPADA TINGGI" : "🟡  PERLU WASPADA";
        StringBuilder s = new StringBuilder(level).append("\n\n");
        s.append("Ditemukan ").append(n).append(" laporan terverifikasi.\n\n");
        for (int i=0;i<n;i++) {
            try {
                JSONObject o=arr.getJSONObject(i);
                s.append("• ").append(o.optString("category","Laporan")).append(" — ").append(o.optString("area","Bali")).append("\n");
                s.append("  ").append(o.optString("story","")).append("\n\n");
            } catch(Exception ignored){}
        }
        s.append("ℹ️ Hasil berdasarkan laporan komunitas, bukan putusan hukum.");
        resultBody.setText(s.toString());
    }

    private String request(String u) throws Exception {
        HttpURLConnection c=(HttpURLConnection)new URL(u).openConnection();
        c.setRequestMethod("GET");
        c.setConnectTimeout(10000); c.setReadTimeout(10000);
        c.setRequestProperty("apikey",SUPABASE_KEY);
        c.setRequestProperty("Authorization","Bearer "+SUPABASE_KEY);
        c.setRequestProperty("Accept","application/json");
        BufferedReader r=new BufferedReader(new InputStreamReader(c.getInputStream()));
        StringBuilder b=new StringBuilder(); String line;
        while((line=r.readLine())!=null)b.append(line);
        r.close(); c.disconnect();
        return b.toString();
    }

    private void addQuick(LinearLayout row,String label,View.OnClickListener l){
        Button b=button(label);
        b.setTextSize(11); b.setMinHeight(0); b.setMinWidth(0);
        row.addView(b,new LinearLayout.LayoutParams(0,dp(54),1){{
            setMargins(dp(4),0,dp(4),0);
        }});
        b.setOnClickListener(l);
    }

    private void action(LinearLayout parent,String label,View.OnClickListener l){
        Button b=button(label); b.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
        parent.addView(b,lp(0,54,0,8)); b.setOnClickListener(l);
    }

    private Button button(String t){
        Button b=new Button(this); b.setText(t); b.setTextColor(white); b.setTextSize(13);
        b.setAllCaps(false); b.setTypeface(null,1); b.setPadding(dp(12),0,dp(12),0);
        b.setBackground(rounded(Color.rgb(22,52,42),14));
        return b;
    }

    private LinearLayout box(){
        LinearLayout x=new LinearLayout(this); x.setOrientation(LinearLayout.VERTICAL);
        x.setPadding(dp(16),dp(16),dp(16),dp(16)); x.setBackground(rounded(card,18)); return x;
    }

    private TextView text(String s,float z,int c){TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(c);return t;}
    private GradientDrawable rounded(int c,int r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(dp(r));g.setStroke(dp(1),Color.rgb(31,61,50));return g;}
    private LinearLayout.LayoutParams lp(int l,int h,int r,int b){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(l==0?-1:dp(l),dp(h));p.setMargins(dp(l==0?0:l),dp(0),dp(r),dp(b));return p;}
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    private void dial(String n){try{startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+n)));}catch(Exception ignored){}}
    private void openWeb(String u){try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(u)));}catch(Exception ignored){}}
    @Override protected void onDestroy(){executor.shutdownNow();super.onDestroy();}
}
