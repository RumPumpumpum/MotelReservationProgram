import java.time.LocalDate;

public class ReservationInfo 
{
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String name;
    private String carNumber;
    private String breakfast;
    private String payment;
    private String memo;

    public ReservationInfo(LocalDate checkIn, 
    		LocalDate checkOut, 
    		String name, 
    		String carNumber, 
    		String breakfast, 
    		String payment, 
    		String memo) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.name = name;
        this.carNumber = carNumber;
        this.breakfast = breakfast;
        this.payment = payment;
        this.memo = memo;
    }

    public LocalDate getCheckIn() 
    {
        return checkIn;
    }

    public LocalDate getCheckOut() 
    {
        return checkOut;
    }

    public String getName() 
    {
        return name;
    }

    public String getCarNumber()
    {
        return carNumber;
    }

    public String getBreakfast() 
    {
        return breakfast;
    }

    public String getPayment()
    {
        return payment;
    }

    public String getMemo()
    {
        return memo;
    }
}