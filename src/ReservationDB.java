import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.text.SimpleDateFormat;

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
	            statement.setString(1, reservationInfo.getRoomNumber());
	            statement.setString(2, reservationInfo.getName());
	            statement.setDate(3, java.sql.Date.valueOf(reservationInfo.getCheckIn()));
	            statement.setDate(4, java.sql.Date.valueOf(reservationInfo.getCheckOut()));
	            statement.setString(7, reservationInfo.getPaymentMethod());
	            statement.setInt(8, reservationInfo.getPaymentAmount());
	            statement.setString(5, reservationInfo.getCarNumber());
	            statement.setInt(6, reservationInfo.getBreakfastCount());
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
	
	public ReservationInfo getReservationInfoByDateAndRoom(String roomNumber, LocalDate date) 
	{
		ReservationInfo reservationInfo = null;
	    try {
	        // SQL 쿼리 작성
	        String sql = "SELECT * FROM reservations WHERE roomNumber = ? AND ? BETWEEN checkInDate AND checkOutDate";
	        PreparedStatement statement = connection.prepareStatement(sql);
	        
	        // 파라미터 설정
	        statement.setString(1, roomNumber);
	        statement.setDate(2, java.sql.Date.valueOf(date));
	        
	        // SQL 실행 및 결과 조회
	        ResultSet resultSet = statement.executeQuery();
	        
	        // 결과 처리
	        if (resultSet.next()) 
	        {
	            // 예약 정보 가져오기
	            String name = resultSet.getString("name");
	            LocalDate checkInDate = resultSet.getObject("checkInDate", LocalDate.class);
	            LocalDate checkOutDate = resultSet.getObject("checkOutDate", LocalDate.class);
	            String paymentMethod = resultSet.getString("paymentMethod");
	            int paymentAmount = resultSet.getInt("paymentAmount");
	            String carNumber = resultSet.getString("carNumber");
	            int breakfastCount = resultSet.getInt("breakfastCount");
	            String memo = resultSet.getString("memo");
	            
	            // ReservationInfo 객체 생성
	            reservationInfo = new ReservationInfo(
	            		roomNumber, 
	            		name, 
	            		checkInDate,
	            		checkOutDate, 
	                    paymentMethod, 
	                    paymentAmount,
	            		carNumber,
	                    breakfastCount,
	                    memo);
	            }
	        } 
	    catch (SQLException e) 
	    {
	        e.printStackTrace();
	    }
	    
	    return reservationInfo;
	}
	
	public void deleteReservationByDateAndRoom(String roomNumber, LocalDate date) {
	    try {
	    	// SQL 쿼리 작성
	        String sql = "DELETE FROM reservations WHERE roomNumber = ? AND ? BETWEEN checkInDate AND checkOutDate";
	        PreparedStatement statement = connection.prepareStatement(sql);

	        // 파라미터 설정
	        statement.setString(1, roomNumber);
	        statement.setDate(2, java.sql.Date.valueOf(date));

	        // SQL 실행
	        int rowsAffected = statement.executeUpdate();
	    	} 
	    catch (SQLException e) 
	    {
	        e.printStackTrace();
	    }
	}


}
