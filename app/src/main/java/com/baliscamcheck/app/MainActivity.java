package com.baliscamcheck.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private static final String SUPABASE_URL =
            "https://mqfipqpmsuapfctotjzo.supabase.co";
    private static final String SUPABASE_KEY =
            "sb_publishable_4sFZXYku2HwgJ7wvHWwKLA_tN-nKxk-";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private LinearLayout content;
    private EditText search;
    private TextView result;
    private Button checkButton;

    private final int BG = Color.rgb(246, 250, 248);
    private final int SURFACE = Color.rgb(255, 255, 255);
    private final int SURFACE_2 = Color.rgb(239, 247, 243);
    private final int BORDER = Color.rgb(218, 231, 224);
    private final int GREEN = Color.rgb(39, 145, 103);
    private final int TEXT = Color.rgb(35, 49, 44);
    private final int MUTED = Color.rgb(105, 123, 115);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        buildScreen();
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(20), dp(20), dp(28));
        scroll.addView(content);

        // HEADER
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView shield = new TextView(this);
        shield.setText("◈");
        shield.setTextColor(GREEN);
        shield.setTextSize(26);
        shield.setGravity(Gravity.CENTER);
        GradientDrawable shieldBg = roundStroke(Color.rgb(235, 247, 241), Color.rgb(194, 222, 208), 14);
        shieldBg.setStroke(dp(1), GREEN);
        shield.setBackground(shieldBg);
        header.addView(shield, size(50, 50));

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);
        titleBox.setPadding(dp(12), 0, 0, 0);

        TextView title = text("BALI SCAMCHECK", 21, TEXT);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        titleBox.addView(title);

        TextView tagline = text("CEK DULU SEBELUM PERCAYA", 10, GREEN);
        tagline.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        titleBox.addView(tagline, margins(0, 3, 0, 0));

        header.addView(titleBox, new LinearLayout.LayoutParams(0, dp(55), 1));

        TextView online = text("● ONLINE", 10, GREEN);
        online.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        header.addView(online);

        content.addView(header);

        // HERO
        LinearLayout hero = card();
        TextView heroTitle = text("Periksa sebelum transfer", 18, TEXT);
        heroTitle.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        hero.addView(heroTitle);

        TextView heroText = text(
                "Cari nomor telepon, rekening, e-wallet, username, atau target mencurigakan.",
                12, MUTED);
        heroText.setLineSpacing(0, 1.15f);
        hero.addView(heroText, margins(0, 7, 0, 0));
        content.addView(hero, margins(0, 22, 0, 0));

        // SEARCH LABEL
        TextView searchLabel = text("TARGET YANG INGIN DIPERIKSA", 10, MUTED);
        searchLabel.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        content.addView(searchLabel, margins(0, 22, 0, 8));

        // SEARCH
        LinearLayout searchRow = new LinearLayout(this);
        searchRow.setGravity(Gravity.CENTER_VERTICAL);

        search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(14);
        search.setTextColor(TEXT);
        search.setHintTextColor(Color.rgb(151, 166, 159));
        search.setHint("Nomor / rekening / username");
        search.setPadding(dp(15), 0, dp(12), 0);
        search.setBackground(roundStroke(SURFACE, BORDER, 14));
        searchRow.addView(search, new LinearLayout.LayoutParams(0, dp(54), 1));

        checkButton = primaryButton("CEK");
        LinearLayout.LayoutParams checkLp = new LinearLayout.LayoutParams(dp(76), dp(54));
        checkLp.setMargins(dp(9), 0, 0, 0);
        searchRow.addView(checkButton, checkLp);

        content.addView(searchRow);

        // QUICK CHECK
        LinearLayout quick = new LinearLayout(this);
        quick.setPadding(0, dp(10), 0, 0);
        addQuick(quick, "NOMOR", "Telepon");
        addQuick(quick, "BANK", "Rekening");
        addQuick(quick, "LINK", "Tautan");
        content.addView(quick);

        // RESULT HEADER
        LinearLayout resultHeader = new LinearLayout(this);
        resultHeader.setGravity(Gravity.CENTER_VERTICAL);

        TextView resultLabel = text("HASIL PEMERIKSAAN", 10, MUTED);
        resultLabel.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        resultHeader.addView(resultLabel, new LinearLayout.LayoutParams(0, dp(24), 1));

        TextView community = text("COMMUNITY REPORTS", 9, Color.rgb(101, 137, 121));
        community.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        resultHeader.addView(community);

        content.addView(resultHeader, margins(0, 22, 0, 7));

        // RESULT CARD
        LinearLayout resultCard = card();
        result = text(
                "Belum ada pemeriksaan.\n\nMasukkan target di atas untuk melihat sinyal risiko.",
                13, MUTED);
        result.setLineSpacing(0, 1.15f);
        resultCard.addView(result);
        content.addView(resultCard);

        // SAFETY ACTIONS
        TextView safetyLabel = text("AKSI KEAMANAN", 10, MUTED);
        safetyLabel.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        content.addView(safetyLabel, margins(0, 22, 0, 8));

        action(content, "BANTUAN DARURAT", "Hubungi Polisi 110", true,
                v -> dial("110"));

        action(content, "LAPORKAN PENIPUAN", "Kirim laporan komunitas", false,
                v -> openWeb("https://mhdaanshoorii.github.io/bali-scamcheck/"));

        action(content, "VERIFIKASI RESMI", "Portal Siber Polda Bali", false,
                v -> openWeb("https://laporsiberbali.id/"));

        // FOOTER
        TextView footer = text(
                "BALI SCAMCHECK  •  COMMUNITY SAFETY NETWORK",
                9, Color.rgb(126, 145, 137));
        footer.setGravity(Gravity.CENTER);
        content.addView(footer, margins(0, 24, 0, 0));

        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        // BOTTOM BAR
        LinearLayout bottom = new LinearLayout(this);
        bottom.setGravity(Gravity.CENTER);
        bottom.setPadding(dp(16), dp(8), dp(16), dp(8));
        bottom.setBackground(roundStroke(Color.rgb(255, 255, 255), BORDER, 0));

        TextView home = navItem("CHECK", true);
        TextView report = navItem("REPORT", false);
        TextView info = navItem("INFO", false);

        bottom.addView(home, new LinearLayout.LayoutParams(0, dp(48), 1));
        bottom.addView(report, new LinearLayout.LayoutParams(0, dp(48), 1));
        bottom.addView(info, new LinearLayout.LayoutParams(0, dp(48), 1));

        report.setOnClickListener(v ->
                openWeb("https://mhdaanshoorii.github.io/bali-scamcheck/"));
        info.setOnClickListener(v ->
                openWeb("https://laporsiberbali.id/"));

        root.addView(bottom, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(65)));

        setContentView(root);

        checkButton.setOnClickListener(v -> checkTarget());
        search.setOnEditorActionListener((v, actionId, event) -> {
            checkTarget();
            return true;
        });
    }

    private void addQuick(LinearLayout row, String code, String label) {
        LinearLayout q = new LinearLayout(this);
        q.setOrientation(LinearLayout.VERTICAL);
        q.setGravity(Gravity.CENTER);
        q.setBackground(roundStroke(SURFACE_2, BORDER, 13));

        TextView top = text(code, 10, GREEN);
        top.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        top.setGravity(Gravity.CENTER);
        q.addView(top);

        TextView bottom = text(label, 10, MUTED);
        bottom.setGravity(Gravity.CENTER);
        q.addView(bottom, margins(0, 2, 0, 0));

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(62), 1);
        p.setMargins(dp(4), 0, dp(4), 0);
        row.addView(q, p);

        q.setOnClickListener(v -> {
            search.requestFocus();
            checkTarget();
        });
    }

    private void action(LinearLayout parent, String title, String sub,
                        boolean danger, View.OnClickListener listener) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(16), dp(10), dp(16), dp(10));
        box.setBackground(roundStroke(
                danger ? Color.rgb(255, 248, 226) : SURFACE_2, BORDER, 15));

        TextView t = text(title, 13, TEXT);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        box.addView(t);

        TextView s = text(sub, 10, danger ? Color.rgb(160, 126, 46) : MUTED);
        box.addView(s, margins(0, 3, 0, 0));

        box.setOnClickListener(listener);
        parent.addView(box, margins(0, 0, 0, 9));
    }

    private TextView navItem(String label, boolean active) {
        TextView v = text(label, 10, active ? GREEN : MUTED);
        v.setGravity(Gravity.CENTER);
        v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return v;
    }

    private void checkTarget() {
        String target = search.getText().toString().trim();

        if (target.length() < 4) {
            search.setError("Masukkan minimal 4 karakter");
            return;
        }

        checkButton.setEnabled(false);
        checkButton.setText("...");
        result.setText("MEMERIKSA DATABASE...\n\nMohon tunggu sebentar.");
        result.setTextColor(MUTED);

        executor.execute(() -> {
            try {
                String encoded = URLEncoder.encode(target, "UTF-8");
                String url = SUPABASE_URL +
                        "/rest/v1/reports?select=target,target_type,category,area,story,created_at" +
                        "&target=ilike.*" + encoded +
                        "*&status=eq.verified&limit=50";

                String response = request(url);
                JSONArray arr = new JSONArray(response);

                runOnUiThread(() -> {
                    checkButton.setEnabled(true);
                    checkButton.setText("CEK");
                    showResults(arr);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    checkButton.setEnabled(true);
                    checkButton.setText("CEK");
                    result.setText(
                            "KONEKSI TIDAK TERSEDIA\n\n" +
                            "Database belum dapat dihubungi. Periksa koneksi internet lalu coba lagi.");
                    result.setTextColor(Color.rgb(178, 128, 45));
                });
            }
        });
    }

    private void showResults(JSONArray arr) {
        if (arr.length() == 0) {
            result.setText(
                    "✓  BELUM ADA LAPORAN\n\n" +
                    "Tidak ditemukan laporan terverifikasi untuk target ini.\n\n" +
                    "Tetap lakukan verifikasi sebelum transfer atau membayar DP.");
            result.setTextColor(GREEN);
            return;
        }

        int n = arr.length();
        String level;

        if (n >= 4) {
            level = "RISIKO TINGGI";
            result.setTextColor(Color.rgb(210, 77, 77));
        } else if (n >= 2) {
            level = "WASPADA TINGGI";
            result.setTextColor(Color.rgb(205, 137, 47));
        } else {
            level = "PERLU WASPADA";
            result.setTextColor(Color.rgb(173, 135, 37));
        }

        StringBuilder out = new StringBuilder();
        out.append(level).append("\n\n");
        out.append("Ditemukan ").append(n)
                .append(" laporan terverifikasi.\n\n");

        for (int i = 0; i < n; i++) {
            try {
                JSONObject o = arr.getJSONObject(i);
                out.append("• ")
                        .append(o.optString("category", "Laporan"))
                        .append("  ·  ")
                        .append(o.optString("area", "Bali"))
                        .append("\n");

                String story = o.optString("story", "");
                if (story.length() > 180) story = story.substring(0, 180) + "...";
                out.append(story).append("\n\n");
            } catch (Exception ignored) {
            }
        }

        out.append("Info: hasil berasal dari laporan komunitas terverifikasi.");
        result.setText(out.toString());
    }

    private String request(String urlString) throws Exception {
        HttpURLConnection c =
                (HttpURLConnection) new URL(urlString).openConnection();

        c.setRequestMethod("GET");
        c.setConnectTimeout(10000);
        c.setReadTimeout(10000);
        c.setRequestProperty("apikey", SUPABASE_KEY);
        c.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
        c.setRequestProperty("Accept", "application/json");

        int code = c.getResponseCode();
        InputStreamReader reader = new InputStreamReader(
                code >= 200 && code < 300
                        ? c.getInputStream()
                        : c.getErrorStream());

        BufferedReader br = new BufferedReader(reader);
        StringBuilder body = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) body.append(line);

        br.close();
        c.disconnect();

        if (code < 200 || code >= 300) {
            throw new Exception("HTTP " + code);
        }

        return body.toString();
    }

    private Button primaryButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(12);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setAllCaps(false);
        b.setGravity(Gravity.CENTER);
        b.setPadding(0, 0, 0, 0);

        GradientDrawable g = rounded(GREEN, 14);
        b.setBackground(g);
        return b;
    }

    private LinearLayout card() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(16), dp(16), dp(16));
        box.setBackground(roundStroke(SURFACE, BORDER, 17));
        return box;
    }

    private TextView text(String value, float size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        return t;
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radius));
        return g;
    }

    private GradientDrawable roundStroke(int color, int stroke, int radius) {
        GradientDrawable g = rounded(color, radius);
        g.setStroke(dp(1), stroke);
        return g;
    }

    private LinearLayout.LayoutParams size(int width, int height) {
        return new LinearLayout.LayoutParams(dp(width), dp(height));
    }

    private LinearLayout.LayoutParams margins(
            int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(dp(left), dp(top), dp(right), dp(bottom));
        return p;
    }

    private int dp(int value) {
        return (int) (value *
                getResources().getDisplayMetrics().density + 0.5f);
    }

    private void dial(String number) {
        try {
            startActivity(new Intent(
                    Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
        } catch (Exception ignored) {
        }
    }

    private void openWeb(String url) {
        try {
            startActivity(new Intent(
                    Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }
}
