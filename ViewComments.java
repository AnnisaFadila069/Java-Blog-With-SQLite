import java.sql.*;

public class ViewComments {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Gunakan: java ViewComments <article_id>");
            return;
        }

        int articleId;
        try {
            articleId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: ID artikel harus berupa angka.");
            return;
        }

        Connection c = null;
        PreparedStatement checkArticleStmt = null;
        PreparedStatement fetchCommentsStmt = null;
        ResultSet articleRs = null;
        ResultSet commentsRs = null;

        try {
            // Koneksi ke SQLite
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db2.sqlite3");

            // Cek apakah artikel ada
            String checkArticleSql = "SELECT title FROM articles WHERE id = ?";
            checkArticleStmt = c.prepareStatement(checkArticleSql);
            checkArticleStmt.setInt(1, articleId);
            articleRs = checkArticleStmt.executeQuery();

            if (!articleRs.next()) {
                System.out.println("❌ Artikel dengan ID " + articleId + " tidak ditemukan.");
                return;
            }

            String articleTitle = articleRs.getString("title");

            // Menampilkan komentar dari artikel
            String fetchCommentsSql = "SELECT commenter_email, comment_text, commented_at FROM comments WHERE article_id = ? ORDER BY commented_at ASC";
            fetchCommentsStmt = c.prepareStatement(fetchCommentsSql);
            fetchCommentsStmt.setInt(1, articleId);
            commentsRs = fetchCommentsStmt.executeQuery();

            System.out.println("=======================================");
            System.out.println("💬 Komentar untuk Artikel: " + articleTitle + " (ID: " + articleId + ")");
            System.out.println("=======================================");

            boolean hasComments = false;
            int commentCount = 0;

            while (commentsRs.next()) {
                hasComments = true;
                commentCount++;
                System.out.println("👤 " + commentsRs.getString("commenter_email"));
                System.out.println("🕒 " + commentsRs.getString("commented_at"));
                System.out.println("💭 " + commentsRs.getString("comment_text"));
                System.out.println("---------------------------------------");
            }

            if (!hasComments) {
                System.out.println("❌ Belum ada komentar pada artikel ini.");
            } else {
                System.out.println("📌 Total Komentar: " + commentCount);
            }

        } catch (Exception e) {
            System.err.println("❌ Terjadi kesalahan: " + e.getMessage());
        } finally {
            // Menutup koneksi
            try {
                if (articleRs != null) articleRs.close();
                if (commentsRs != null) commentsRs.close();
                if (checkArticleStmt != null) checkArticleStmt.close();
                if (fetchCommentsStmt != null) fetchCommentsStmt.close();
                if (c != null) c.close();
            } catch (SQLException ex) {
                System.err.println("❌ Gagal menutup koneksi: " + ex.getMessage());
            }
        }
    }
}
