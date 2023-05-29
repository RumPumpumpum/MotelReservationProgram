import java.time.LocalDate;

public class ReservationInfo 
{
	private String roomNumber;
    private String name;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String paymentMethod;
    private int paymentAmount;
    private String carNumber;
    private int breakfastCount;
    private String memo;

    public ReservationInfo(
    		String roomNumber,
    		String name,
    		LocalDate checkIn, 
    		LocalDate checkOut, 
    		String paymentMethod, 
    		int paymentAmount, 
    		String carNumber, 
    		int breakfastCount, 
    		String memo) {
    	this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.name = name;
        this.carNumber = carNumber;
        this.breakfastCount = breakfastCount;
        this.paymentMethod = paymentMethod;
        this.paymentAmount = paymentAmount;
        this.memo = memo;
    }

    public String getRoomNumber() 
    {
        return roomNumber;
    }
    
    public String getName() 
    {
        return name;
    }
    
    public LocalDate getCheckIn() 
    {
        return checkIn;
    }

    public LocalDate getCheckOut() 
    {
        return checkOut;
    }

    public String getPaymentMethod()
    {
        return paymentMethod;
    }
    
    public int getPaymentAmount()
    {
        return paymentAmount;
    }

    public int getBreakfastCount() 
    {
        return breakfastCount;
    }

    public String getCarNumber()
    {
        return carNumber;
    }

    public String getMemo()
    {
        return memo;
    }
}