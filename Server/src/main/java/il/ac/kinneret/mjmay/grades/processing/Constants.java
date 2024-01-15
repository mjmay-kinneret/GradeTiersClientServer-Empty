package il.ac.kinneret.mjmay.grades.processing;

public class Constants {

    public static String connectionStringBooklets = "jdbc:sqlite:booklets.db";
    public static String connectionStringGrades = "jdbc:sqlite:grades.db";

    public static void setConnectionStringBooklets (String conn){
        connectionStringBooklets = conn;
    }

    public static void setConnectionStringGrades (String conn)
    {
        connectionStringGrades = conn;
    }

    public static String bookletQuery = "SELECT S.tz as 'TZ', S.sname as 'Student Name', C.cname as 'Course Name', C.year" +
            " as 'Year', C.semester as 'Semester', T.bookno as 'Booklet Number'" +
            " FROM TestBooklets T, Students S, Courses C" +
            " WHERE S.tz = T.tz AND C.cid = T.cid" +
            " AND T.tz = ?" +
            " ORDER BY bookno";

    public static String BOOKLET_NUMBER_COLUMN = "Booklet Number";
    public static String TZ_COLUMN = "TZ";
    public static String STUDENT_NAME_COLUMN = "Student Name";
    public static String COURSE_NAME_COLUMN = "Course Name";
    public static String YEAR_COLUMN = "YEAR";
    public static String SEMESTER_COLUMN = "SEMESTER";


    public static String gradesQuery = "SELECT bookno as 'Booklet Number', checked as 'Checked?', grade as 'Grade'"+
            " FROM Grades" +
            " WHERE bookno = ?";

    public static String CHECKED_COLUMN = "Checked?";
    public static String GRADE_COLUMN = "Grade";

    public static String resultsFormat = "%-10d %-10s %-35s %-4d %-2d %3d %-3s %3s";
}
