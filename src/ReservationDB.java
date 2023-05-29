import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReservationDB 
{	
	private Connection connection;
	
    private static final String DB_URL = "jdbc:mysql://localhost:3306/reservationprogram";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin";

    
    public void connectToDatabase() 
    {
        try
        {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // DB 연결
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } 
        catch (ClassNotFoundException e)
        {
            // JDBC 드라이버 로드 실패
            e.printStackTrace();
        } 
        catch (SQLException e)
        {
            // DB 연결 실패
            e.printStackTrace();
        }
    }

	public void SaveReservationToDatabase(ReservationInfo reservationInfo) 
	{
		 try 
	        {
	            // PreparedStatement를 사용하여 SQL 쿼리 작성
	            String sql = "INSERT INTO reservations (roomNumber, name, checkInDate, checkOutDate, paymentMethod, paymentAmount, carNumber, breakfastCount, memo)"
	            		+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	            PreparedStatement statement = connection.prepareStatement(sql);
	            
	            // 예약 정보 설정
	            statement.setString(1, reservationInfo.getroomNumber());
	            statement.setDate(2, java.sql.Date.valueOf(reservationInfo.getCheckIn()));
	            statement.setDate(3, java.sql.Date.valueOf(reservationInfo.getCheckOut()));
	            statement.setString(4, reservationInfo.getName());
	            statement.setString(5, reservationInfo.getCarNumber());
	            statement.setInt(6, reservationInfo.getBreakfastCount());
	            statement.setString(7, reservationInfo.getPaymentMethod());
	            statement.setInt(8, reservationInfo.getPaymentAmount());
	            statement.setString(9, reservationInfo.getMemo());
	            
	            // SQL 실행
	            statement.executeUpdate();      
	        } 
	        catch (SQLException e) 
	        {
	        	 // 예약 정보 저장 실패
	            e.printStackTrace();
	        }
	
	}
}
