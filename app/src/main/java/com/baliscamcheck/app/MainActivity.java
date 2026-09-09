package com.baliscamcheck.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MainActivity extends Activity {

    private static final String API =
            "https://mqfipqpmsuapfctotjzo.supabase.co";
    private static final String KEY =
            "sb_publishable_4sFZXYku2HwgJ7wvHWwKLA_tN-nKxk-";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final int BG = Color.rgb(248,251,249);
    private final int GREEN = Color.rgb(35,145,99);
    private final int DARK = Color.rgb(28,48,42);
    private final int MUTED = Color.rgb(91,112,104);
    private final int BORDER = Color.rgb(218,231,224);
    private final int MINT = Color.rgb(239,248,244);
    private final int BLUE = Color.rgb(49,118,211);
    private final int PURPLE = Color.rgb(126,83,202);

    private EditText search;
    private TextView resultTitle, resultText;
    private Button check;
    private LinearLayout content;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(246,250,248));
        getWindow().setNavigationBarColor(Color.WHITE);
        build();
    }

    private void build() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20),dp(14),dp(20),dp(18));

        // ===== PREMIUM HEADER =====
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView logo = text("◇", 35, GREEN);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(roundStroke(Color.rgb(237,248,242),
                Color.rgb(63,155,116), 18));
        header.addView(logo, size(72,72));

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);
        brand.setPadding(dp(13),0,0,0);

        TextView name = text("BALI\nSCAMCHECK", 25, DARK);
        name.setTypeface(null,1);
        brand.addView(name);

        TextView tag = text("Cek dulu sebelum percaya", 13, GREEN);
        brand.addView(tag, margins(0,3,0,0));
        header.addView(brand, new LinearLayout.LayoutParams(0,dp(78),1));

        LinearLayout online = new LinearLayout(this);
        online.setOrientation(LinearLayout.VERTICAL);
        online.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        TextView c1 = text("Bersama",10,DARK);
        TextView c2 = text("Komunitas Bali",10,DARK);
        TextView c3 = text("yang Lebih Aman",10,DARK);
        TextView on = text("● Online",12,GREEN);
        on.setTypeface(null,1);
        online.addView(c1); online.addView(c2); online.addView(c3);
        online.addView(on,margins(0,4,0,0));
        header.addView(online,new LinearLayout.LayoutParams(dp(115),dp(78)));

        content.addView(header);

        // ===== BALI HERO ART =====
        BaliSky sky = new BaliSky(this);
        content.addView(sky, new LinearLayout.LayoutParams(-1,dp(92)));

        // ===== SEARCH CARD =====
        LinearLayout scan = card();
        scan.setPadding(dp(18),dp(17),dp(18),dp(18));

        LinearLayout scanTitle = new LinearLayout(this);
        scanTitle.setGravity(Gravity.CENTER_VERTICAL);

        TextView magnify = text("⌕",30,GREEN);
        magnify.setGravity(Gravity.CENTER);
        magnify.setBackground(round( Color.rgb(235,248,242),50));
        scanTitle.addView(magnify,size(50,50));

        LinearLayout scanWords = new LinearLayout(this);
        scanWords.setOrientation(LinearLayout.VERTICAL);
        scanWords.setPadding(dp(12),0,0,0);
        TextView st = text("Periksa sebelum transfer",19,DARK);
        st.setTypeface(null,1);
        scanWords.addView(st);
        TextView sd = text("Cari nomor telepon, rekening, e-wallet, username,\natau link yang mencurigakan.",12,MUTED);
        sd.setLineSpacing(0,1.1f);
        scanWords.addView(sd,margins(0,4,0,0));
        scanTitle.addView(scanWords,new LinearLayout.LayoutParams(0,dp(67),1));
        scan.addView(scanTitle);

        LinearLayout sr = new LinearLayout(this);
        sr.setGravity(Gravity.CENTER_VERTICAL);
        search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(14);
        search.setTextColor(DARK);
        search.setHintTextColor(Color.rgb(145,160,154));
        search.setHint("Masukkan target di sini...");
        search.setPadding(dp(14),0,dp(10),0);
        search.setBackground(roundStroke(Color.WHITE,Color.rgb(204,217,211),16));
        sr.addView(search,new LinearLayout.LayoutParams(0,dp(56),1));

        check = primary("CEK  →");
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(dp(102),dp(56));
        cp.setMargins(dp(10),0,0,0);
        sr.addView(check,cp);
        scan.addView(sr,margins(0,14,0,0));

        LinearLayout quick = new LinearLayout(this);
        quick.setPadding(0,dp(11),0,0);
        quick(quick,"●","Nomor","Telepon",GREEN);
        quick(quick,"▣","Rekening","Bank / E-wallet",BLUE);
        quick(quick,"↗","Link","URL / Tautan",PURPLE);
        scan.addView(quick);

        content.addView(scan,margins(0,0,0,14));

        // ===== SAFETY BANNER =====
        LinearLayout safe = card();
        safe.setOrientation(LinearLayout.HORIZONTAL);
        safe.setGravity(Gravity.CENTER_VERTICAL);
        safe.setBackground(roundStroke(Color.rgb(255,249,232),
                Color.rgb(241,229,194),18));

        TextView shield = text("★",23,Color.rgb(170,126,28));
        shield.setGravity(Gravity.CENTER);
        shield.setBackground(round(Color.rgb(255,240,203),50));
        safe.addView(shield,size(48,48));

        LinearLayout sw = new LinearLayout(this);
        sw.setOrientation(LinearLayout.VERTICAL);
        sw.setPadding(dp(12),0,0,0);
        TextView safeTitle=text("Transaksi aman, liburan nyaman",14,DARK);
        safeTitle.setTypeface(null,1);
        sw.addView(safeTitle);
        sw.addView(text("Jangan mudah percaya. Selalu cek sebelum transfer.",11,MUTED),
                margins(0,4,0,0));
        safe.addView(sw,new LinearLayout.LayoutParams(0,dp(55),1));
        safe.addView(text("›",27,MUTED));
        content.addView(safe);

        // ===== RESULT HEADER =====
        LinearLayout rh=new LinearLayout(this);
        rh.setGravity(Gravity.CENTER_VERTICAL);
        TextView rt=text("Hasil Pemeriksaan",19,DARK);
        rt.setTypeface(null,1);
        rh.addView(rt,new LinearLayout.LayoutParams(0,dp(42),1));
        TextView history=text("◷  Lihat Riwayat",11,GREEN);
        history.setTypeface(null,1);
        rh.addView(history);
        content.addView(rh,margins(0,12,0,0));

        LinearLayout result=card();
        result.setPadding(dp(17),dp(17),dp(17),dp(17));
        LinearLayout rline=new LinearLayout(this);
        rline.setGravity(Gravity.TOP);
        TextView checkmark=text("✓",25,GREEN);
        checkmark.setGravity(Gravity.CENTER);
        checkmark.setBackground(round(Color.rgb(230,247,239),50));
        rline.addView(checkmark,size(48,48));

        LinearLayout rw=new LinearLayout(this);
        rw.setOrientation(LinearLayout.VERTICAL);
        rw.setPadding(dp(13),0,0,0);
        resultTitle=text("BELUM ADA LAPORAN",14,GREEN);
        resultTitle.setTypeface(null,1);
        rw.addView(resultTitle);
        resultText=text("Tidak ditemukan laporan terverifikasi\nuntuk target ini.\n\nTetap lakukan verifikasi sebelum transfer atau\nmembayar DP.",12,MUTED);
        resultText.setLineSpacing(0,1.15f);
        rw.addView(resultText,margins(0,6,0,0));
        rline.addView(rw,new LinearLayout.LayoutParams(0,-2,1));
        result.addView(rline);
        content.addView(result);

        // ===== QUICK ACTIONS =====
        TextView al=text("Aksi Cepat",19,DARK);
        al.setTypeface(null,1);
        content.addView(al,margins(0,16,0,8));

        LinearLayout actions=new LinearLayout(this);
        quickAction(actions,"☎","Bantuan\nDarurat","Hubungi Polisi 110",
                Color.rgb(226,75,67),v->dial("110"));
        quickAction(actions,"▣","Laporkan\nPenipuan","Kirim laporan komunitas",
                BLUE,v->open("https://mhdaanshoorii.github.io/bali-scamcheck/"));
        quickAction(actions,"◇","Verifikasi\nResmi","Portal Siber Polda Bali",
                GREEN,v->open("https://laporsiberbali.id/"));
        content.addView(actions);

        // ===== COMMUNITY =====
        LinearLayout community=card();
        community.setGravity(Gravity.CENTER_VERTICAL);
        TextView people=text("♣",25,GREEN);
        people.setGravity(Gravity.CENTER);
        people.setBackground(round(Color.rgb(230,247,239),50));
        community.addView(people,size(50,50));

        LinearLayout cw=new LinearLayout(this);
        cw.setOrientation(LinearLayout.VERTICAL);
        cw.setPadding(dp(12),0,0,0);
        TextView ct=text("Didukung oleh Komunitas",14,DARK);
        ct.setTypeface(null,1);
        cw.addView(ct);
        cw.addView(text("Informasi lebih aman, lebih cepat,\nlebih banyak manfaat untuk semua.",11,MUTED),
                margins(0,3,0,0));
        community.addView(cw,new LinearLayout.LayoutParams(0,dp(56),1));
        TextView hash=text("#BaliLebihAman",10,GREEN);
        hash.setTypeface(null,1);
        community.addView(hash);
        content.addView(community,margins(0,12,0,8));

        // Footer
        TextView footer=text("BALI SCAMCHECK  •  Community Safety Network",9,
                Color.rgb(130,148,140));
        footer.setGravity(Gravity.CENTER);
        content.addView(footer,margins(0,12,0,0));

        scroll.addView(content);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        // ===== BOTTOM NAV =====
        LinearLayout nav=new LinearLayout(this);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(dp(10),dp(7),dp(10),dp(7));
        nav.setBackground(roundStroke(Color.WHITE,BORDER,20));
        navItem(nav,"⌂","Beranda",true);
        navItem(nav,"▤","Laporan",false);
        navItem(nav,"♢","Tips",false);
        navItem(nav,"⚙","Pengaturan",false);
        root.addView(nav,new LinearLayout.LayoutParams(-1,dp(72)));

        setContentView(root);
        check.setOnClickListener(v->doCheck());
    }

    private void quick(LinearLayout row,String icon,String title,String sub,int color){
        LinearLayout q=new LinearLayout(this);
        q.setOrientation(LinearLayout.VERTICAL);
        q.setGravity(Gravity.CENTER_VERTICAL);
        q.setPadding(dp(13),dp(10),dp(8),dp(10));
        q.setBackground(roundStroke(
                color==BLUE?Color.rgb(241,247,255):
                color==PURPLE?Color.rgb(248,243,255):Color.rgb(238,249,243),
                BORDER,15));
        TextView i=text(icon,19,color); i.setGravity(Gravity.CENTER);
        i.setBackground(round(Color.argb(35,80,180,130),50));
        q.addView(i,size(34,34));
        TextView t=text(title,12,DARK);t.setTypeface(null,1);
        q.addView(t,margins(0,5,0,0));
        q.addView(text(sub,9,MUTED),margins(0,2,0,0));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(84),1);
        p.setMargins(dp(4),0,dp(4),0);
        row.addView(q,p);
    }

    private void quickAction(LinearLayout row,String icon,String title,String sub,
                             int color,View.OnClickListener listener){
        LinearLayout q=new LinearLayout(this);
        q.setOrientation(LinearLayout.VERTICAL);
        q.setPadding(dp(12),dp(12),dp(10),dp(11));
        q.setBackground(roundStroke(
                color==BLUE?Color.rgb(241,247,255):
                color==GREEN?Color.rgb(239,249,244):Color.rgb(255,243,242),
                color==BLUE?Color.rgb(210,226,248):
                color==GREEN?Color.rgb(210,233,220):Color.rgb(245,213,211),18));
        TextView ic=text(icon,20,color);ic.setGravity(Gravity.CENTER);
        ic.setBackground(round(Color.argb(30,80,150,100),50));
        q.addView(ic,size(42,42));
        TextView t=text(title,12,DARK);t.setTypeface(null,1);
        q.addView(t,margins(0,8,0,0));
        TextView s=text(sub,9,MUTED);s.setLineSpacing(0,1.1f);
        q.addView(s,margins(0,3,0,0));
        q.setOnClickListener(listener);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(145),1);
        p.setMargins(dp(4),0,dp(4),0);
        row.addView(q,p);
    }

    private void navItem(LinearLayout nav,String icon,String label,boolean active){
        LinearLayout n=new LinearLayout(this);
        n.setOrientation(LinearLayout.VERTICAL);
        n.setGravity(Gravity.CENTER);
        TextView i=text(icon,23,active?GREEN:MUTED);i.setGravity(Gravity.CENTER);
        n.addView(i);
        TextView l=text(label,10,active?GREEN:MUTED);l.setTypeface(null,1);
        n.addView(l);
        if(active)n.addView(text("•",16,GREEN));
        nav.addView(n,new LinearLayout.LayoutParams(0,dp(58),1));
    }

    private void doCheck(){
        String target=search.getText().toString().trim();
        if(target.length()<4){search.setError("Masukkan minimal 4 karakter");return;}
        check.setEnabled(false);check.setText("...");
        resultTitle.setText("MEMERIKSA...");
        resultTitle.setTextColor(MUTED);
        resultText.setText("Sedang memeriksa database komunitas.");
        executor.execute(()->{
            try{
                String e=URLEncoder.encode(target,"UTF-8");
                String u=API+"/rest/v1/reports?select=category,area,story&target=ilike.*"+e+"*&status=eq.verified&limit=50";
                JSONArray a=new JSONArray(request(u));
                runOnUiThread(()->{
                    check.setEnabled(true);check.setText("CEK  →");
                    if(a.length()==0){
                        resultTitle.setText("✓  BELUM ADA LAPORAN");
                        resultTitle.setTextColor(GREEN);
                        resultText.setText("Tidak ditemukan laporan terverifikasi untuk target ini.\n\nTetap lakukan verifikasi sebelum transfer atau membayar DP.");
                    }else{
                        resultTitle.setText(a.length()>=4?"RISIKO TINGGI":
                                a.length()>=2?"WASPADA TINGGI":"PERLU WASPADA");
                        resultTitle.setTextColor(a.length()>=4?Color.rgb(201,74,74):
                                a.length()>=2?Color.rgb(190,126,37):Color.rgb(166,127,31));
                        StringBuilder s=new StringBuilder("Ditemukan ")
                                .append(a.length()).append(" laporan terverifikasi.\n\n");
                        for(int i=0;i<a.length();i++){
                            JSONObject o=a.getJSONObject(i);
                            s.append("• ").append(o.optString("category","Laporan"))
                             .append(" · ").append(o.optString("area","Bali")).append("\n")
                             .append(o.optString("story","")).append("\n\n");
                        }
                        s.append("Hasil berdasarkan laporan komunitas terverifikasi.");
                        resultText.setText(s.toString());
                    }
                });
            }catch(Exception ex){
                runOnUiThread(()->{
                    check.setEnabled(true);check.setText("CEK  →");
                    resultTitle.setText("KONEKSI GAGAL");
                    resultTitle.setTextColor(Color.rgb(190,126,37));
                    resultText.setText("Tidak dapat menghubungi database. Coba lagi.");
                });
            }
        });
    }

    private String request(String u)throws Exception{
        HttpURLConnection c=(HttpURLConnection)new URL(u).openConnection();
        c.setRequestMethod("GET");c.setConnectTimeout(10000);c.setReadTimeout(10000);
        c.setRequestProperty("apikey",KEY);
        c.setRequestProperty("Authorization","Bearer "+KEY);
        BufferedReader r=new BufferedReader(new InputStreamReader(
                c.getResponseCode()<300?c.getInputStream():c.getErrorStream()));
        StringBuilder b=new StringBuilder();String l;
        while((l=r.readLine())!=null)b.append(l);
        r.close();int code=c.getResponseCode();c.disconnect();
        if(code>=300)throw new Exception("HTTP "+code);return b.toString();
    }

    private Button primary(String s){
        Button b=new Button(this);b.setText(s);b.setTextColor(Color.WHITE);
        b.setTextSize(12);b.setTypeface(null,1);b.setAllCaps(false);
        b.setPadding(0,0,0,0);b.setBackground(round(GREEN,16));return b;
    }

    private LinearLayout card(){
        LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(16),dp(16),dp(16),dp(16));
        l.setBackground(roundStroke(Color.WHITE,BORDER,20));return l;
    }

    private TextView text(String s,float z,int c){
        TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(c);return t;
    }

    private GradientDrawable round(int c,int r){
        GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(dp(r));return g;
    }

    private GradientDrawable roundStroke(int c,int stroke,int r){
        GradientDrawable g=round(c,r);g.setStroke(dp(1),stroke);return g;
    }

    private LinearLayout.LayoutParams size(int w,int h){
        return new LinearLayout.LayoutParams(dp(w),dp(h));
    }

    private LinearLayout.LayoutParams margins(int l,int t,int r,int b){
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(dp(l),dp(t),dp(r),dp(b));return p;
    }

    private int dp(int v){return(int)(v*getResources().getDisplayMetrics().density+.5f);}
    private void dial(String n){try{startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+n)));}catch(Exception ignored){}}
    private void open(String u){try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(u)));}catch(Exception ignored){}}
    @Override protected void onDestroy(){executor.shutdownNow();super.onDestroy();}

    // Lightweight Bali-inspired header illustration; no external image dependency.
    private class BaliSky extends View{
        Paint p=new Paint(3);
        public BaliSky(android.content.Context c){super(c);p.setStrokeCap(Paint.Cap.ROUND);}
        protected void onDraw(Canvas c){
            super.onDraw(c);
            LinearGradient g=new LinearGradient(0,0,getWidth(),getHeight(),
                    Color.rgb(240,249,252),Color.rgb(235,247,239),Shader.TileMode.CLAMP);
            p.setShader(g);c.drawRect(0,0,getWidth(),getHeight(),p);p.setShader(null);
            p.setColor(Color.rgb(196,215,198));
            Path island=new Path();
            island.moveTo(getWidth()*0.48f,getHeight());
            island.lineTo(getWidth()*0.67f,getHeight()*0.42f);
            island.lineTo(getWidth()*0.77f,getHeight()*0.63f);
            island.lineTo(getWidth(),getHeight()*0.72f);
            island.lineTo(getWidth(),getHeight());
            c.drawPath(island,p);
            p.setColor(Color.rgb(137,166,146));
            c.drawRect(getWidth()*0.70f,getHeight()*0.38f,getWidth()*0.705f,getHeight()*0.80f,p);
            for(int i=0;i<5;i++)c.drawRect(getWidth()*0.665f-i*dp(3),
                    getHeight()*(0.35f+i*.055f),
                    getWidth()*.75f+i*dp(3),
                    getHeight()*(0.38f+i*.055f),p);
            p.setColor(Color.argb(70,35,145,99));
            c.drawCircle(getWidth()*.86f,getHeight()*.22f,dp(26),p);
        }
    }
}
