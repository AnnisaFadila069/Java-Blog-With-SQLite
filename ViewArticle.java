import java.sql.*;

public class ViewArticle {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Gunakan: java ViewArticle <article_id> atau <author_email>");
            return;
        }

        String input = args[0];
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Koneksi ke SQLite
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db2.sqlite3");

            // Menentukan query berdasarkan input
            String sql;
            boolean isNumeric = input.matches("\\d+"); // Cek apakah input adalah angka (ID artikel)

            if (isNumeric) {
                // Jika input berupa angka, cari berdasarkan ID artikel
                sql = "SELECT id, author_email, created_at, title, content FROM articles WHERE id = ?";
                stmt = c.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(input));
            } else {
                // Jika input berupa email, cari semua artikel dari penulis tersebut
                sql = "SELECT id, author_email, created_at, title, content FROM articles WHERE author_email = ? ORDER BY created_at DESC";
                stmt = c.prepareStatement(sql);
                stmt.setString(1, input);
            }

            // Eksekusi query
            rs = stmt.executeQuery();

            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;
                int articleId = rs.getInt("id");

                System.out.println("=======================================");
                System.out.println("📝 Artikel #" + articleId);
                System.out.println("✍️  Penulis  : " + rs.getString("author_email"));
                System.out.println("📅 Tanggal   : " + rs.getString("created_at"));
                System.out.println("📌 Judul     : " + rs.getString("title"));
                System.out.println("📖 Konten    :\n" + rs.getString("content"));

                // Tampilkan jumlah komentar untuk artikel ini
                int commentCount = getCommentCount(c, articleId);
                System.out.println("💬 Jumlah Komentar: " + commentCount);

                // Tampilkan jumlah like untuk artikel ini
                int likeCount = getLikeCount(c, articleId);
                System.out.println("❤️ Jumlah Like: " + likeCount);

                System.out.println("=======================================\n");
            }

            if (!hasResults) {
                System.out.println("❌ Tidak ditemukan artikel untuk input: " + input);
            }

        } catch (Exception e) {
            System.err.println("❌ Terjadi kesalahan: " + e.getMessage());
        } finally {
            // Menutup koneksi
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (c != null) c.close();
            } catch (SQLException ex) {
                System.err.println("❌ Gagal menutup koneksi: " + ex.getMessage());
            }
        }
    }

    // Fungsi untuk mendapatkan jumlah komentar dari sebuah artikel
    private static int getCommentCount(Connection c, int articleId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            String sql = "SELECT COUNT(*) FROM comments WHERE article_id = ?";
            stmt = c.prepareStatement(sql);
            stmt.setInt(1, articleId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Gagal mendapatkan jumlah komentar: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException ex) {
                System.err.println("❌ Gagal menutup koneksi komentar: " + ex.getMessage());
            }
        }

        return count;
    }

    // Fungsi untuk mendapatkan jumlah like dari sebuah artikel
    private static int getLikeCount(Connection c, int articleId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int count = 0;

        try {
            String sql = "SELECT COUNT(*) FROM likes WHERE article_id = ?";
            stmt = c.prepareStatement(sql);
            stmt.setInt(1, articleId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("❌ Gagal mendapatkan jumlah like: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException ex) {
                System.err.println("❌ Gagal menutup koneksi like: " + ex.getMessage());
            }
        }

        return count;
    }
}
