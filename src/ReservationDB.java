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
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;

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
	            statement.setString(5, reservationInfo.getPaymentMethod());
	            statement.setInt(6, reservationInfo.getPaymentAmount());
	            statement.setString(7, reservationInfo.getCarNumber());
	            statement.setInt(8, reservationInfo.getBreakfastCount());
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
	
	public ReservationInfo getReservationInfoByDateAndRoom(String roomNumber, LocalDate selectDate, LocalTime checkOutTime, String infoType) {
		ReservationInfo leaveReservationInfo = null;
		ReservationInfo reservationInfo = null;
	    try 
	    {
	        // SQL 쿼리 작성
	        String sql = "SELECT * FROM reservations WHERE roomNumber = ? AND ? BETWEEN checkInDate AND checkOutDate";
	        PreparedStatement statement = connection.prepareStatement(sql);

	        // 파라미터 설정
	        statement.setString(1, roomNumber);
	        statement.setDate(2, java.sql.Date.valueOf(selectDate));

	        // SQL 실행 및 결과 조회
	        ResultSet resultSet = statement.executeQuery();
	        
            // 현재 날짜/시간
            LocalTime currentTime = LocalTime.now();
            LocalDate currentDate = LocalDate.now();

	        while (resultSet.next()) 
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

	            
	            // 체크아웃 날짜가 기준날짜와 같으면 퇴실예정 예약으로 저장
	            if (checkOutDate.isEqual(selectDate))
	            {
	               leaveReservationInfo = new ReservationInfo(
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
	            // 아니라면 일반 예약으로 저장
	            else
	            {
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
	        
	        // 기준 날짜와 현재 날짜가 같다면
	        if(currentDate.isEqual(selectDate))
	        {
	        	
	        	if(infoType == "leave")
	        	{
		        	return leaveReservationInfo;
	        	}
	        	
	        	else
	        	{
		        	return reservationInfo;
	        	}

	        }
	        // 기준 날짜가 현재 날짜가 아니면 일반예약 표시
        	if(infoType == "leave")
        	{
	        	return null;
        	}
	        else
	        {
	        	return reservationInfo;
	        }
	    }
	    catch (SQLException e) 
	    {
	        e.printStackTrace();
	    }

	    return reservationInfo;
	}

	public void deleteReservationByDateAndRoom(String roomNumber, LocalDate date)
	{
	    try {
	    	// SQL 쿼리 작성
	        String sql = "DELETE FROM reservations WHERE roomNumber = ? AND checkOutDate = ?";
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
	
	public boolean isReservationDateValid(ReservationInfo reservationInfo) 
	{
	    try {
	        PreparedStatement statement = connection.prepareStatement(
	                "SELECT checkInDate, checkOutDate FROM reservations WHERE roomNumber = ?"
	        );
	        statement.setString(1, reservationInfo.getRoomNumber());
	        ResultSet resultSet = statement.executeQuery();

	        while (resultSet.next()) {
	            LocalDate existingCheckInDate = resultSet.getObject("checkInDate", LocalDate.class);
	            LocalDate existingCheckOutDate = resultSet.getObject("checkOutDate", LocalDate.class);

	            // 입력한 체크인 날짜와 체크아웃 날짜가 이미 입력된 예약의 체크인과 체크아웃 날짜 사이에 겹치는지 확인
	            if ((reservationInfo.getCheckIn().isBefore(existingCheckOutDate) && reservationInfo.getCheckOut().isAfter(existingCheckInDate))
	            		|| ((reservationInfo.getCheckIn().isEqual(existingCheckInDate) && reservationInfo.getCheckOut().isEqual(existingCheckOutDate))))
	            		{

	                return false;
	            }
	        }

	        resultSet.close();
	        statement.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return true;
	}
	
	public LocalTime getCheckOutTime()
	{    
		LocalTime checkOutTime = null;
        try 
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT checkOutTime FROM reservation_options");

            if (resultSet.next()) {
                java.sql.Time sqlTime = resultSet.getTime("checkOutTime");
                checkOutTime = sqlTime.toLocalTime();
            }
            
            resultSet.close();
            statement.close();
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        
        return checkOutTime;
    }
	
    public void setCheckOutTime(LocalTime checkOutTime) 
    {
        try 
        {
            PreparedStatement statement = connection.prepareStatement("UPDATE reservation_options SET checkOutTime = ?");
            statement.setString(1, checkOutTime.toString());
            statement.executeUpdate();
            statement.close();
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    public List<ReservationInfo> getReservationsByRoomNumber(String roomNumber) 
    {
        List<ReservationInfo> reservations = new ArrayList<>();
        try {
            // SQL 쿼리 작성
            String sql = "SELECT * FROM reservations WHERE roomNumber = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            // 파라미터 설정
            statement.setString(1, roomNumber);

            // SQL 실행 및 결과 조회
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
	            // 예약 정보 가져오기
	            String name = resultSet.getString("name");
	            LocalDate checkInDate = resultSet.getObject("checkInDate", LocalDate.class);
	            LocalDate checkOutDate = resultSet.getObject("checkOutDate", LocalDate.class);
	            String paymentMethod = resultSet.getString("paymentMethod");
	            int paymentAmount = resultSet.getInt("paymentAmount");
	            String carNumber = resultSet.getString("carNumber");
	            int breakfastCount = resultSet.getInt("breakfastCount");
	            String memo = resultSet.getString("memo");

                // 예약 정보 객체 생성
	            ReservationInfo reservationInfo = new ReservationInfo(
                		roomNumber,
                		name, 
                		checkInDate, 
                		checkOutDate, 
                   		paymentMethod,
                		paymentAmount,
                		carNumber, 
                		breakfastCount, 
                		memo);                
                reservations.add(reservationInfo);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return reservations;
    }
    
    public List<ReservationInfo> getReservationsByRoomNumberAndDate(String roomNumber, LocalDate searchStart, LocalDate searchEnd) 
    {
        List<ReservationInfo> reservations = new ArrayList<>();
        try {
            // SQL 쿼리 작성
        	String sql = "SELECT * FROM reservations WHERE roomNumber = ? AND (checkInDate BETWEEN ? AND ? OR checkOutDate BETWEEN ? AND ?)";
        	PreparedStatement statement = connection.prepareStatement(sql);
            // 파라미터 설정
        	statement.setString(1, roomNumber);
        	statement.setDate(2, java.sql.Date.valueOf(searchStart));
        	statement.setDate(3, java.sql.Date.valueOf(searchEnd));
        	statement.setDate(4, java.sql.Date.valueOf(searchStart));
        	statement.setDate(5, java.sql.Date.valueOf(searchEnd));

            // SQL 실행 및 결과 조회
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
	            // 예약 정보 가져오기
	            String name = resultSet.getString("name");
	            LocalDate checkInDate = resultSet.getObject("checkInDate", LocalDate.class);
	            LocalDate checkOutDate = resultSet.getObject("checkOutDate", LocalDate.class);
	            String paymentMethod = resultSet.getString("paymentMethod");
	            int paymentAmount = resultSet.getInt("paymentAmount"); 
	            String carNumber = resultSet.getString("carNumber");
	            int breakfastCount = resultSet.getInt("breakfastCount");
	            String memo = resultSet.getString("memo");

                // 예약 정보 객체 생성
	            ReservationInfo reservationInfo = new ReservationInfo(
                		roomNumber,
                		name, 
                		checkInDate, 
                		checkOutDate, 
                   		paymentMethod,
                		paymentAmount,
                		carNumber, 
                		breakfastCount, 
                		memo);                
                reservations.add(reservationInfo);
            }
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return reservations;
    }
    
    public int ShowTotalAmount(LocalDate selectDate)
    {
    	int totalPaymentAmount = 0;
        try {
            // SQL 쿼리 작성
        	String sql = "SELECT * FROM reservations WHERE ? BETWEEN checkInDate AND checkOutDate";
        	PreparedStatement statement = connection.prepareStatement(sql);
            // 파라미터 설정
        	statement.setDate(1, java.sql.Date.valueOf(selectDate));

            // SQL 실행 및 결과 조회
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) 
            {
	            // 예약 정보 가져오기
	            LocalDate checkInDate = resultSet.getObject("checkInDate", LocalDate.class);
	            LocalDate checkOutDate = resultSet.getObject("checkOutDate", LocalDate.class);
	            int paymentAmount = resultSet.getInt("paymentAmount");
	            
	            long stayDate = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
	            
	            // 현재 시간이 체크아웃 시간 이전이면
	            if((LocalTime.now().isBefore(getCheckOutTime())))
	            {
	            	// 퇴실날짜가 기준날짜와 같은(퇴실예정인) 방의 금액을 더함 ( 더하는 금액은 받은 금액 / 머문 일수)
	            	if(checkOutDate.isEqual(selectDate))
	            	{
	            		totalPaymentAmount += paymentAmount / stayDate;
	            	}
	            }
	            // 현재 시간이 체크아웃 시간 이후면
	            else if((LocalTime.now().isAfter(getCheckOutTime())))
	            {
	            	// 퇴실날짜가 기준날짜와 같지 않은(퇴실예정이 아닌) 방의 금액을 더함
	            	if(!checkOutDate.isEqual(selectDate))
	            	{
	            		totalPaymentAmount += paymentAmount / stayDate;
	            	}
	            }
            }
        }
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return totalPaymentAmount;
    }
    
    public int ShowTotalBreakfast(LocalDate selectDate)
    {
    	int totalBreakfastCount = 0;
        try {
            // SQL 쿼리 작성
        	String sql = "SELECT * FROM reservations WHERE ? BETWEEN checkInDate AND checkOutDate";
        	PreparedStatement statement = connection.prepareStatement(sql);
            // 파라미터 설정
        	statement.setDate(1, java.sql.Date.valueOf(selectDate));

            // SQL 실행 및 결과 조회
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) 
            {
	            // 예약 정보 가져오기
	            LocalDate checkInDate = resultSet.getObject("checkInDate", LocalDate.class);
	            LocalDate checkOutDate = resultSet.getObject("checkOutDate", LocalDate.class);
	            int breakfastCount = resultSet.getInt("breakfastCount");
	            	            
	            // 현재 시간이 체크아웃 시간 이전이면
	            if((LocalTime.now().isBefore(getCheckOutTime())))
	            {
	            	// 퇴실날짜가 기준날짜와 같은(퇴실예정인) 방의 금액을 더함 ( 더하는 금액은 받은 금액 / 머문 일수)
	            	if(checkOutDate.isEqual(selectDate))
	            	{
	            		totalBreakfastCount += breakfastCount;
	            	}
	            }
	            // 현재 시간이 체크아웃 시간 이후면
	            else if((LocalTime.now().isAfter(getCheckOutTime())))
	            {
	            	// 퇴실날짜가 기준날짜와 같지 않은(퇴실예정이 아닌) 방의 금액을 더함
	            	if(!checkOutDate.isEqual(selectDate))
	            	{
	            		totalBreakfastCount += breakfastCount;
	            	}
	            }
            }
        }
        catch (SQLException e) 
        {
            e.printStackTrace();
        }

        return totalBreakfastCount;
    }
    
    public List<String> ShowTodayParkingInfo(LocalDate selectDate)
    {
        List<String> parkingInfoList = new ArrayList<>();

        try 
        {
            // SQL 쿼리 작성
            String sql = "SELECT * FROM reservations WHERE ? BETWEEN checkInDate AND checkOutDate";
            PreparedStatement statement = connection.prepareStatement(sql);
            // 파라미터 설정
            statement.setDate(1, java.sql.Date.valueOf(selectDate));

            // SQL 실행 및 결과 조회
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) 
            {
                // 예약 정보 가져오기
                LocalDate checkInDate = resultSet.getObject("checkInDate", LocalDate.class);
                LocalDate checkOutDate = resultSet.getObject("checkOutDate", LocalDate.class);
                String roomNumber = resultSet.getString("roomNumber");
                String carNumber = resultSet.getString("carNumber");

                // 현재 시간이 체크아웃 시간 이전이면 & 현재날짜와 기준날짜가 같으면
                if (LocalTime.now().isBefore(getCheckOutTime()) && LocalDate.now().isEqual(selectDate))
                {
                    // 퇴실날짜가 기준날짜와 같은(퇴실예정인) 방의 정보를 리스트에 추가
                    if (checkOutDate.isEqual(selectDate))
                    {
                        String parkingInfo = "방 번호: " + roomNumber + ", 차 번호: " + carNumber;
                        parkingInfoList.add(parkingInfo);
                    }
                }
                // 현재 시간이 체크아웃 시간 이후면 || 현재날짜와 기준날짜가 다르면
                else if (LocalTime.now().isAfter(getCheckOutTime()) || !LocalDate.now().isEqual(selectDate)) 
                {
                    // 퇴실날짜가 기준날짜와 같지 않은(퇴실예정이 아닌) 방의 정보를 리스트에 추가
                    if (!checkOutDate.isEqual(selectDate))
                    {
                        String parkingInfo = "방 번호: " + roomNumber + ", 차 번호: " + carNumber;
                        parkingInfoList.add(parkingInfo);
                    }
                }
            }
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return parkingInfoList;
    }
    
   public void UpdateReservationInfo(ReservationInfo reservationInfo, ReservationInfo newReservationInfo)
   {
	   try {
	        // 예약 업데이트 쿼리 실행
	        String updateQuery = "UPDATE reservations SET roomNumber = ?, "
	                + "name = ?, "
	                + "checkInDate = ?, "
	                + "checkOutDate = ?, "
	                + "paymentMethod = ?, "
	                + "paymentAmount = ?, "
	                + "carNumber = ?, "
	                + "breakfastCount = ?, "
	                + "memo = ?"
	                + "WHERE roomNumber = ? AND "
	                + "name = ? AND "
	                + "checkInDate = ? AND "
	                + "checkOutDate = ? AND "
	                + "paymentMethod = ? AND "
	                + "paymentAmount = ? AND "
	                + "carNumber = ? AND "
	                + "breakfastCount = ? AND "
	                + "memo = ?";
	        
	        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
	        updateStatement.setString(1, newReservationInfo.getRoomNumber());
	        updateStatement.setString(2, newReservationInfo.getName());
	        updateStatement.setDate(3, java.sql.Date.valueOf(newReservationInfo.getCheckIn()));
	        updateStatement.setDate(4, java.sql.Date.valueOf(newReservationInfo.getCheckOut()));
	        updateStatement.setString(5, newReservationInfo.getPaymentMethod());
	        updateStatement.setInt(6, newReservationInfo.getPaymentAmount());
	        updateStatement.setString(7, newReservationInfo.getCarNumber());
	        updateStatement.setInt(8, newReservationInfo.getBreakfastCount());
	        updateStatement.setString(9, newReservationInfo.getMemo());
	        updateStatement.setString(10, reservationInfo.getRoomNumber());
	        updateStatement.setString(11, reservationInfo.getName());
	        updateStatement.setDate(12, java.sql.Date.valueOf(reservationInfo.getCheckIn()));
	        updateStatement.setDate(13, java.sql.Date.valueOf(reservationInfo.getCheckOut()));
	        updateStatement.setString(14, reservationInfo.getPaymentMethod());
	        updateStatement.setInt(15, reservationInfo.getPaymentAmount());
	        updateStatement.setString(16, reservationInfo.getCarNumber());
	        updateStatement.setInt(17, newReservationInfo.getBreakfastCount());
	        updateStatement.setString(18, reservationInfo.getMemo());

	        updateStatement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();  // 예외 정보를 출력하거나 다른 예외 처리 방식을 선택할 수 있습니다.
	    }
   }

}
