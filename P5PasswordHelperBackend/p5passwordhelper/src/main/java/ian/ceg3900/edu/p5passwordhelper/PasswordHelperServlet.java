import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/** Google App Engine servlet that checks if our password exists in public password dumps.
 */
public class PasswordHelperServlet extends HttpServlet {

    private String common_password_response = "Password is too similar to other common passwords!";
    private String uncommon_password_response = "Password did not match any common passwords!";

    private double passwordSimilarityFactor = 0.9;
    private int differenceThreshold = 3; /// The accepted length difference to say that two passwords aren't similar.

    private boolean similarPassword(String actualPassword, String commonPassword) {
        if (commonPassword.length() >= actualPassword.length()) {
            // The common password is the same size or greater than the actual password,
            // So we're going to test to see if they're equal or the actual password is contained in the common.
            return actualPassword.equals(commonPassword) || commonPassword.contains(actualPassword);
        } else if (actualPassword.length() - commonPassword.length() >= differenceThreshold) {
            // The common password is shorter enough than the actual password that we're calling them different.
            return false;
        }

        int left_partition_index = (int) (commonPassword.length() * passwordSimilarityFactor);
        int right_partition_index = (int) (commonPassword.length() * (1.0 - passwordSimilarityFactor));

        String left_sixty = commonPassword.substring(0, left_partition_index);
        String right_forty = commonPassword.substring(right_partition_index);

        if (actualPassword.contains(left_sixty) || actualPassword.contains(right_forty)) {
            System.out.println(left_sixty);
            System.out.println(right_forty);
            System.out.println(actualPassword);
            return true;
        } else {
            return false;
        }
    }

    private boolean checkCommonPasswords(final String password) throws IOException {
        InputStream stream = this.getServletContext().getResourceAsStream("/WEB-INF/res/password-dump.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        int numberChecks = 0;
        String currentPassword;

        while ((currentPassword = reader.readLine()) != null ) {
            if (similarPassword(password, currentPassword)) {
                System.out.println("Ran: " + numberChecks + " checks");
                return true;
            }
            numberChecks++;
        }

        System.out.println("Ran: " + numberChecks + " checks");
        return false;

//        String similarPassword = reader.lines().parallel().reduce((foundPass, commonPassword) -> {
//            if (!foundPass.equals("")) {
//                return foundPass;
//            } else if (similarPassword(password, commonPassword)) {
//                return commonPassword;
//            } else {
//                return "";
//            }
//        }).get();
//
//        return !similarPassword.equals("");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(405);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String password = request.getPathInfo();
        if (password.startsWith("/")) {
            // Get rid of a trailing slash from the URI.
            password = password.substring(1);
        }
        PrintWriter writer = response.getWriter();

        if (checkCommonPasswords(password)) {
            writer.println(common_password_response);
        } else {
            writer.println(uncommon_password_response);
        }
    }
}
