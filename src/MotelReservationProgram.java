import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import javax.swing.Timer;
import javax.swing.BoxLayout;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;

public class MotelReservationProgram extends JFrame
{
	private JLabel currentDateLabel;
	private JLabel currentTimeLabel;
	private JLabel selectDateLabel;
	private JLabel checkOutTimeLabel;
	private JLabel totalAmountLabel;
	private JLabel totalBreakfastLabel;
    
    public LocalDate selectDate;

    private Map<String, JPanel> roomPanelMap = new HashMap<>();
    private Map<String, JLabel> roomLabelMap = new HashMap<>();
    private Map<String, JLabel> roomGuestMap = new HashMap<>();
    
    private JTextPane logTextPane;
    private JScrollPane scrollPane;
    
    private Timer timer;
    
    private ReservationDB reservationDB;
    
    int floor7Start = 701;
    int floor7End = 711;
    int floor8Start = 801;
    int floor8End = 813;
    int floor9Start = 901;
    int floor9End = 913;
    

    
    public MotelReservationProgram() 
    {
        // DB 연동
        reservationDB = new ReservationDB();
        reservationDB.connectToDatabase(); 
        
        setTitle("모텔 객실 예약 관리 프로그램");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 정보 표시 레이블들 생성
        currentDateLabel = new JLabel();
        currentTimeLabel = new JLabel();
        selectDateLabel = new JLabel();
        checkOutTimeLabel = new JLabel();
        totalAmountLabel = new JLabel();
        totalBreakfastLabel = new JLabel();
        
        // 폰트 설정
        Font bigFont = new Font(Font.DIALOG, Font.PLAIN, 20);
        currentDateLabel.setFont(bigFont);
        currentTimeLabel.setFont(bigFont);
        selectDateLabel.setFont(bigFont);
        checkOutTimeLabel.setFont(bigFont);
       	totalAmountLabel.setFont(bigFont);
       	totalBreakfastLabel.setFont(bigFont);
        
        // 선택한 날짜 초기화
        selectDate = LocalDate.now();
        
        // 현재 날짜와 시간 초기화
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        
        
        // 프레임을 전체화면으로 고정
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // 레이아웃 설정
        setLayout(new GridLayout(1, 2));
        
        // 좌측 패널
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(3, 1));

        // 각 층별로 패널 생성
        JPanel floor1Panel = createFloorPanel("9", floor9Start, floor9End);
        JPanel floor2Panel = createFloorPanel("8", floor8Start, floor8End);
        JPanel floor3Panel = createFloorPanel("7", floor7Start, floor7End);        

        // 층별 패널을 FlowLayout으로 설정하여 다음 줄로 넘어가도록 함
        floor1Panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        floor2Panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        floor3Panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        leftPanel.add(floor1Panel);
        leftPanel.add(floor2Panel);
        leftPanel.add(floor3Panel);
        
        // 우측 패널
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(1, 2));
        
        JPanel infoPanel = new JPanel();
        JPanel logPanel = new JPanel();
        
        // info 패널 설정
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        rightPanel.add(infoPanel, BorderLayout.CENTER);
        
        infoPanel.add(currentDateLabel);
        infoPanel.add(currentTimeLabel);    
       
        infoPanel.add(Box.createVerticalStrut(100)); // 수직 간격 추가
        infoPanel.add(selectDateLabel);
        
        // 1일을 더하는 버튼
        PlusButton(infoPanel);
  
        // 1일을 빼는 버튼
        MinusButton(infoPanel);
        
        // 기준날짜 변경 버튼
        setSelectButton(infoPanel);
                
        infoPanel.add(Box.createVerticalStrut(100)); // 수직 간격 추가
        infoPanel.add(checkOutTimeLabel);
        
        // 체크아웃 시간 변경 버튼
        setCheckOutTimeButton(infoPanel);
        
        // 예약 목록 확인 버튼
        infoPanel.add(Box.createVerticalStrut(100)); // 수직 간격 추가
        showReservationListButton(infoPanel, "roomType");
        showReservationListButton(infoPanel, "dateType");

        infoPanel.add(Box.createVerticalStrut(100)); // 수직 간격 추가
        infoPanel.add(totalAmountLabel);
        infoPanel.add(totalBreakfastLabel);
        
        infoPanel.add(Box.createVerticalStrut(100)); // 수직 간격 추가
        createTodayParkingButton(infoPanel);

        
        // log 패널 설정
        logTextPane = new JTextPane();
        logTextPane.setEditable(false); // 편집 불가능하도록 설정
        scrollPane = new JScrollPane(logTextPane);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(leftPanel);
        add(rightPanel);
  
        TickTimer(); // 타이머 호출
        setVisible(true);
    }

    public JPanel createFloorPanel(String floor, int startRoomNumber, int endRoomNumber) 
    {	
        JPanel floorPanel = new JPanel();
        floorPanel.setLayout(new GridLayout(0, 7)); // 열 개수를 설정하여 다음 줄로 넘어가도록 함

        JLabel floorLabel = new JLabel(floor + "층");
        Font bigFont = new Font(Font.DIALOG, Font.PLAIN, 40);
        floorLabel.setFont(bigFont);
        floorPanel.add(floorLabel);
        
        for (int i = startRoomNumber; i <= endRoomNumber; i++) 
        {
            String roomNumber = Integer.toString(i);
 
        	
            JPanel roomPanel = new JPanel();
            roomPanelMap.put(roomNumber, roomPanel); // roomPanel을 roomPanelMap에 추가
            roomPanel.setPreferredSize(new Dimension(130, 150)); // 조정된 높이
            roomPanel.setLayout(new BorderLayout());  
            
            JLabel roomLabel = new JLabel();
            roomLabelMap.put(roomNumber, roomLabel); // roomLabel을 roomLabelMap에 추가
            roomLabel.setText(Integer.toString(i) + "호"); 
            
            JLabel roomGuestLabel = new JLabel();
            roomGuestMap.put(roomNumber, roomGuestLabel); // roomLabel을 roomLabelMap에 추가  

            roomPanel.add(roomLabel, BorderLayout.NORTH);
            roomPanel.add(roomGuestLabel, BorderLayout.CENTER);
                        
            JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
            ReserveButton(buttonPanel, roomNumber); // 예약 버튼 추가
            CheckReservationButton(buttonPanel, roomNumber); // 예약 확인 버튼 추가
            roomPanel.add(buttonPanel, BorderLayout.SOUTH);

            Border roomPanelBorder = BorderFactory.createLineBorder(Color.BLACK);
            roomPanel.setBorder(roomPanelBorder);

            floorPanel.add(roomPanel);            
        }

        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BorderLayout());
        outerPanel.add(floorPanel, BorderLayout.NORTH);

        Border floorPanelBorder = BorderFactory.createLineBorder(Color.BLACK);
        outerPanel.setBorder(floorPanelBorder);

        return outerPanel;
    }
    
    public void AppendLog(String message, Color color) 
    {
        // 로그 텍스트 스타일을 설정하기 위한 스타일 컬렉션 생성
        StyledDocument doc = logTextPane.getStyledDocument();
        Style style = logTextPane.addStyle("LogStyle", null);

        // 메시지에 해당하는 스타일을 적용
        StyleConstants.setForeground(style, color);
 
        try 
        {
            // 현재 날짜 및 시간을 포맷팅하여 로그에 추가
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = formatter.format(new Date());

            // 로그 메시지와 날짜를 추가
            doc.insertString(doc.getLength(), "[" + formattedDate + "] " + message + "\n", style);
        } 
        catch (BadLocationException e) 
        {
            e.printStackTrace();
        }
    }
   
    private void AppendReservationLog(ReservationInfo reservationInfo,Color color) 
    {
        AppendLog("======================================", color);
        AppendLog("예약자 명: " + reservationInfo.getName(), color);
        AppendLog("체크인 날짜: " + reservationInfo.getCheckIn(), color);
        AppendLog("체크아웃 날짜: " + reservationInfo.getCheckOut(), color);
        AppendLog("결제 수단: " + reservationInfo.getPaymentMethod(), color);
        AppendLog("결제 금액: " + reservationInfo.getPaymentAmount(), color);
        AppendLog("차량번호: " + reservationInfo.getCarNumber(), color);
        AppendLog("조식 인원: " + reservationInfo.getBreakfastCount(), color);
        AppendLog("메모: " + reservationInfo.getMemo(), color);
        AppendLog("======================================", color);
    }   
    
    private JTextArea ShowReservationInfo(String roomNumber, ReservationInfo reservationInfo)
    {
    	// 예약 정보를 편집 불가능한 상태로 출력하여 확인하는 메시지 대화상자 표시
        StringBuilder message = new StringBuilder();
        message.append("객실 번호: ").append(roomNumber).append("\n");
        message.append("이름: ").append(reservationInfo.getName()).append("\n");
        message.append("체크인 날짜: ").append(reservationInfo.getCheckIn()).append("\n");
        message.append("체크아웃 날짜: ").append(reservationInfo.getCheckOut()).append("\n");
        message.append("결제 수단: ").append(reservationInfo.getPaymentMethod()).append("\n");
        message.append("결제 금액: ").append(reservationInfo.getPaymentAmount()).append("\n");
        message.append("차량 번호: ").append(reservationInfo.getCarNumber()).append("\n");
        message.append("조식 인원: ").append(reservationInfo.getBreakfastCount()).append("\n");
        message.append("메모: ").append(reservationInfo.getMemo()).append("\n");

        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);
        
        return textArea;
    }
    
    private void DeleteReservationInfo(String roomNumber, ReservationInfo reservationInfo)
    {
    	// 버튼 클릭 로그
    	AppendLog("예약 삭제 - 객실 번호: " + roomNumber, Color.RED);
        AppendReservationLog(reservationInfo, Color.RED);

        reservationDB.deleteReservationByDateAndRoom(roomNumber, reservationInfo.getCheckOut());
        JOptionPane.showMessageDialog(
                MotelReservationProgram.this,
                "예약 정보가 삭제되었습니다.",
                "예약 정보 삭제",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private LocalDate ParseDate(String date) 
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }
    
    private LocalTime ParseTime(String time) 
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalTime.parse(time, formatter);
    }
    
    private void ShowGuestInfo(int floorStart, int floorEnd)
    {
    	ReservationInfo leaveGuestInfo = null;
    	ReservationInfo arriveGuestInfo = null;
    	
        for (int i = floorStart; i <= floorEnd; i++) 
        {
        	String roomNumber = Integer.toString(i);
        	leaveGuestInfo = reservationDB.getReservationInfoByDateAndRoom(roomNumber, selectDate, reservationDB.getCheckOutTime(), "leave");      
        	arriveGuestInfo = reservationDB.getReservationInfoByDateAndRoom(roomNumber, selectDate, reservationDB.getCheckOutTime(), null);     

            // roomGuest에 이름 넣기
            if (arriveGuestInfo != null) 
            {       
            	if(leaveGuestInfo!=null && selectDate.isEqual(LocalDate.now()) && reservationDB.getCheckOutTime().isAfter(LocalTime.now()))
            	{
                	// HTML 태그를 사용하여 줄 바꿈을 적용
                	roomGuestMap.get(roomNumber).setText("<html>" + 
                	leaveGuestInfo.getName() +
                	" (오늘퇴실)" +
                	"<br>" +
                	"----------" +
                	"<br>" + 
                	arriveGuestInfo.getName() +
                  	" (입실예정)" +
                	"<br>" + 
                	"입실: " +
                	arriveGuestInfo.getCheckIn() + 
                	"<br>" + 
                	"퇴실: " +
                	arriveGuestInfo.getCheckOut() + 
                	"</html>");
            	}
            	else if(leaveGuestInfo==null && 
            			arriveGuestInfo.getCheckIn().isEqual(LocalDate.now()) && 
            			reservationDB.getCheckOutTime().isAfter(LocalTime.now()) && 
            			selectDate.isEqual(LocalDate.now()))
            	{
                	// HTML 태그를 사용하여 줄 바꿈을 적용
                	roomGuestMap.get(roomNumber).setText("<html>" + 
                	arriveGuestInfo.getName() + 
                	"(입실예정) " +
                	"<br>" + 
                	"입실: " +
                	arriveGuestInfo.getCheckIn() + 
                	"<br>" + 
                	"퇴실: " +
                	arriveGuestInfo.getCheckOut() + 
                	"</html>");
            	}
            	else
            	{
                	// HTML 태그를 사용하여 줄 바꿈을 적용
                	roomGuestMap.get(roomNumber).setText("<html>" + 
                	arriveGuestInfo.getName() + 
                	"<br>" + 
                	"입실: " +
                	arriveGuestInfo.getCheckIn() + 
                	"<br>" + 
                	"퇴실: " +
                	arriveGuestInfo.getCheckOut() + 
                	"</html>");
            	}
            } 
            else if(leaveGuestInfo != null && reservationDB.getCheckOutTime().isAfter(LocalTime.now()))
            {
            	roomGuestMap.get(roomNumber).setText("<html>" + 
            	leaveGuestInfo.getName() +
            	" (오늘퇴실)" +
            	"<br>" + 
            	"입실: " +
            	leaveGuestInfo.getCheckIn() + 
            	"<br>" + 
            	"퇴실: " +
            	leaveGuestInfo.getCheckOut() + 
            	"</html>");
        	}
            else
            {
            	roomGuestMap.get(roomNumber).setText("");
            }
        	
        }
    }
    
    private void PlusButton(JPanel panel)
    {
        JButton plusButton = new JButton("다음날");
        plusButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                selectDate = selectDate.plusDays(1);
                AppendLog("다음날 클릭- 이동한 날짜: " + selectDate, Color.BLUE);
            }
        });
        panel.add(plusButton);
    }
    
    private void MinusButton(JPanel panel)
    {
        JButton minusButton = new JButton("이전날");
        minusButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                selectDate = selectDate.minusDays(1);
                AppendLog("이전날 클릭- 이동한 날짜: " + selectDate, Color.BLUE);
            }
        });
        panel.add(minusButton);
    }
    
    private void setSelectButton(JPanel panel)
    {
    	JButton setSelectDateButton = new JButton("기준날짜 변경");
        
        setSelectDateButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                // 버튼 클릭 로그
                AppendLog("기준날짜 설정버튼 클릭", Color.BLACK);

                JPanel panel = new JPanel(new GridLayout(1, 1)); // 입력 폼을 위한 그리드 레이아웃

                JTextField selectDateField = new JTextField();

                panel.add(new JLabel("기준 날짜: "));
                panel.add(selectDateField);
                selectDateField.setText(selectDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                int result = JOptionPane.showConfirmDialog(
                        MotelReservationProgram.this,
                        panel,
                        "기준날짜 변경",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE );

                if (result == JOptionPane.OK_OPTION) 
                {
                    String newSelectDate = selectDateField.getText();
                    selectDate = ParseDate(newSelectDate);
                    
                    // 로그 출력
                    AppendLog("기준날짜 변경 " + selectDate, Color.BLUE);
                 }
            }
        });
        panel.add(setSelectDateButton); // info패널에 버튼 추가
    }
    
    private void setCheckOutTimeButton(JPanel panel)
    {
JButton setCheckOutTimeButton = new JButton("체크아웃 시간 변경");
        
        setCheckOutTimeButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                // 버튼 클릭 로그
                AppendLog("체크아웃 시간 설정버튼 클릭", Color.BLACK);

                JPanel panel = new JPanel(new GridLayout(1, 1)); // 입력 폼을 위한 그리드 레이아웃

                JTextField checkOutTimeField = new JTextField();

                panel.add(new JLabel("체크아웃 시간: "));
                panel.add(checkOutTimeField);
                checkOutTimeField.setText(reservationDB.getCheckOutTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

                int result = JOptionPane.showConfirmDialog(
                        MotelReservationProgram.this,
                        panel,
                        "시간 변경",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE );

                if (result == JOptionPane.OK_OPTION) 
                {
                    String newCheckOutTime = checkOutTimeField.getText();
                    reservationDB.setCheckOutTime(ParseTime(newCheckOutTime));
                    
                    // 로그 출력
                    AppendLog("체크아웃 시간 변경" + reservationDB.getCheckOutTime(), Color.BLUE);
                 }
            }
        });
        panel.add(setCheckOutTimeButton); // info패널에 버튼 추가
    }
    
    private void ReserveButton(JPanel panel, String roomNumber)
    {
    	JButton reserveButton = new JButton("예약");
        reserveButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                // 버튼 클릭 로그
                AppendLog("예약 버튼 클릭 - 객실 번호: " + roomNumber, Color.BLACK);

                JPanel panel = new JPanel(new GridLayout(9, 2)); // 입력 폼을 위한 그리드 레이아웃
                JLabel roomNumberLabel = new JLabel("객실 번호: " + roomNumber); // 객실 번호 표시
                JTextField nameField = new JTextField();
                JTextField checkInField = new JTextField();
                JTextField checkOutField = new JTextField();
                JTextField carNumberField = new JTextField();
                JTextField breakfastCountField = new JTextField();
                JTextField paymentMethodField = new JTextField();
                JTextField paymentAmountField = new JTextField();
                JTextField memoField = new JTextField();

                panel.add(roomNumberLabel);
                panel.add(new JLabel()); // 빈 레이블 추가하여 정렬 맞춤
                panel.add(new JLabel("이름:"));
                panel.add(nameField);
                panel.add(new JLabel("체크인 날짜:"));
                panel.add(checkInField);
                if(LocalTime.now().isBefore(reservationDB.getCheckOutTime()) && LocalDate.now().isEqual(selectDate))
                {
                    checkInField.setText(selectDate.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // 어제날짜 미리입력
                }
                else
                {
                    checkInField.setText(selectDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // 기준 날짜 미리 입력
                }
                panel.add(new JLabel("체크아웃 날짜:"));
                panel.add(checkOutField);
                if(LocalTime.now().isBefore(reservationDB.getCheckOutTime()) && LocalDate.now().isEqual(selectDate))
                {
                    checkOutField.setText(selectDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // 어제날짜 + 1일 미리 입력
                }
                else
                {
                    checkOutField.setText(selectDate.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))); // 기준 날짜 + 1일 미리 입력
                }
                panel.add(new JLabel("차량 번호:"));
                panel.add(carNumberField);
                panel.add(new JLabel("조식 인원:"));
                panel.add(breakfastCountField);
                panel.add(new JLabel("결제 수단:"));
                panel.add(paymentMethodField);
                panel.add(new JLabel("결제 금액:"));
                panel.add(paymentAmountField);
                panel.add(new JLabel("메모:"));
                panel.add(memoField);

                int result = JOptionPane.showConfirmDialog(
                        MotelReservationProgram.this,
                        panel,
                        "예약 정보 입력",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE );

                if (result == JOptionPane.OK_OPTION) 
                {
                    String name = nameField.getText();
                    String checkIn = checkInField.getText();
                    String checkOut = checkOutField.getText();
                    String carNumber = carNumberField.getText();
                    int breakfastCount = Integer.parseInt(breakfastCountField.getText());
                    String paymentMethod = paymentMethodField.getText();
                    int paymentAmount = Integer.parseInt(paymentAmountField.getText());
                    String memo = memoField.getText();

                    // 예약 정보를 저장하거나 처리 String -> LocalDate
                    LocalDate checkInDate = ParseDate(checkIn);
                    LocalDate checkOutDate = ParseDate(checkOut);
                    
                    // 체크아웃 날짜가 체크인 날짜보다 빠르거나 같은 경우 에러
                    if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate))
                    {
                        JOptionPane.showMessageDialog(
                            MotelReservationProgram.this,
                            "체크아웃 날짜가 잘못 되었습니다.",
                            "오류",
                            JOptionPane.ERROR_MESSAGE);
                        
                        return; // 예약 정보 저장 및 처리를 하지 않고 종료
                    }

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
                    
                    // 중복예약 여부 확인
                    if(reservationDB.isReservationDateValid(reservationInfo))
                    {
                        // DB에 예약정보 넣기
                        reservationDB.SaveReservationToDatabase(reservationInfo);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(
                                MotelReservationProgram.this,
                                "예약기간 내에 이미 예약이 존재합니다.",
                                "오류",
                                JOptionPane.ERROR_MESSAGE);
                            
                        	AppendLog("예약실패 (사유: 예약 기간 중복) - 객실 번호: " + roomNumber, Color.RED);
                            return; // 예약 정보 저장 및 처리를 하지 않고 종료
                    }

                    
                    // 예약 정보 로그 출력
                    AppendLog("예약 완료 - 객실 번호: " + roomNumber, Color.BLUE);
                    AppendReservationLog(reservationInfo, Color.BLUE);
                 }
            }
        });
        panel.add(reserveButton);
    }
    
    private void CheckReservationButton(JPanel panel, String roomNumber) 
    {
        JButton checkReservationButton = new JButton("예약 확인"); // 추가된 버튼
        checkReservationButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                // 버튼 클릭 로그
                AppendLog("예약 확인 버튼 클릭 - 객실 번호: " + roomNumber, Color.BLACK);

                ReservationInfo leaveReservationInfo = reservationDB.getReservationInfoByDateAndRoom(roomNumber, selectDate, reservationDB.getCheckOutTime(),"leave");
                ReservationInfo reservationInfo = reservationDB.getReservationInfoByDateAndRoom(roomNumber, selectDate, reservationDB.getCheckOutTime(), null);

                if (leaveReservationInfo != null && reservationInfo != null && LocalTime.now().isBefore(reservationDB.getCheckOutTime())) 
                {
                    JTextArea leaveReservationTextArea = ShowReservationInfo(roomNumber, leaveReservationInfo);
                    JTextArea reservationTextArea = ShowReservationInfo(roomNumber, reservationInfo);

                    JButton leaveDeleteButton = new JButton("삭제 (퇴실 예정)");
                    leaveDeleteButton.addActionListener(new ActionListener() 
                    {
                        public void actionPerformed(ActionEvent e) 
                        {
                            DeleteReservationInfo(roomNumber, leaveReservationInfo);
                            // 예약 정보 확인 창 닫기
                            Window window = SwingUtilities.getWindowAncestor(leaveDeleteButton);
                            window.dispose();
                        }
                    });
                    
                    JButton deleteButton = new JButton("삭제 (입실 예정)");
                    deleteButton.addActionListener(new ActionListener() 
                    {
                        public void actionPerformed(ActionEvent e) 
                        {
                        	DeleteReservationInfo(roomNumber, reservationInfo);
                            // 예약 정보 확인 창 닫기
                            Window window = SwingUtilities.getWindowAncestor(deleteButton);
                            window.dispose();
                        }
                    });
                    
                    JButton leaveEditButton = new JButton("편집 (퇴실 예정)");
                    leaveEditButton.addActionListener(new ActionListener() 
                    {
                        public void actionPerformed(ActionEvent e) 
                        {
                            EditReservationInfo(panel, leaveReservationInfo);
                            // 편집 창 닫기
                            Window window = SwingUtilities.getWindowAncestor(leaveEditButton);
                            window.dispose();
                        }
                    });
                    
                    JButton editButton = new JButton("편집 (입실 예정)");
                    editButton.addActionListener(new ActionListener() 
                    {
                        public void actionPerformed(ActionEvent e) 
                        {
                        	EditReservationInfo(panel, reservationInfo);
                            // 편집 정보 확인 창 닫기
                            Window window = SwingUtilities.getWindowAncestor(editButton);
                            window.dispose();
                        }
                    });

                    JPanel leaveReservationPanel = new JPanel(new BorderLayout());
                    leaveReservationPanel.add(leaveReservationTextArea, BorderLayout.CENTER);
                    leaveReservationPanel.add(leaveDeleteButton, BorderLayout.SOUTH);
                    leaveReservationPanel.add(leaveEditButton, BorderLayout.NORTH);
                    
                    JPanel reservationPanel = new JPanel(new BorderLayout());
                    reservationPanel.add(reservationTextArea, BorderLayout.CENTER);
                    reservationPanel.add(deleteButton, BorderLayout.SOUTH);
                    reservationPanel.add(editButton, BorderLayout.NORTH);


                    JPanel messagePanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 간격 조정
                    messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패널 간격 조정
                    messagePanel.add(leaveReservationPanel);
                    messagePanel.add(reservationPanel);

                    JOptionPane.showMessageDialog(
                            MotelReservationProgram.this,
                            messagePanel,
                            "예약 정보 확인",
                            JOptionPane.INFORMATION_MESSAGE);
                } 
                else if (reservationInfo != null || leaveReservationInfo != null) 
                {
   
                    JTextArea leaveReservationTextArea = null;
                    JTextArea reservationTextArea = null;

                    if(reservationInfo!=null)
                	{
                		 reservationTextArea = ShowReservationInfo(roomNumber, reservationInfo);
                	}
                    else
                	{
                        leaveReservationTextArea = ShowReservationInfo(roomNumber, leaveReservationInfo);
                	}
                	

                    
                    JButton deleteButton = new JButton("삭제");
                    deleteButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                        	if(reservationInfo!=null)
                        	{
                                DeleteReservationInfo(roomNumber, reservationInfo);
                        	}
                        	else
                        	{
                                DeleteReservationInfo(roomNumber, leaveReservationInfo);
                        	}
                            // 예약 정보 확인 창 닫기
                            Window window = SwingUtilities.getWindowAncestor(deleteButton);
                            window.dispose();
                        }
                    });
                    
                    JButton editButton = new JButton("편집");
                    editButton.addActionListener(new ActionListener() 
                    {
                        public void actionPerformed(ActionEvent e) 
                        {
                        	if(reservationInfo!=null)
                        	{
                            	EditReservationInfo(panel, reservationInfo);
                        	}
                        	else
                        	{
                            	EditReservationInfo(panel, leaveReservationInfo);
                        	}
                            // 편집 정보 확인 창 닫기
                            Window window = SwingUtilities.getWindowAncestor(editButton);
                            window.dispose();
                        }
                    });

                    JPanel reservationPanel = new JPanel(new BorderLayout());
                 	if(reservationInfo!=null)
                	{
                        reservationPanel.add(reservationTextArea, BorderLayout.CENTER);
                	}
                 	else
                	{
                        reservationPanel.add(leaveReservationTextArea, BorderLayout.CENTER);
                	}

                    reservationPanel.add(deleteButton, BorderLayout.SOUTH);
                    reservationPanel.add(editButton, BorderLayout.NORTH);


                    JPanel messagePanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 간격 조정
                    messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패널 간격 조정
                    messagePanel.add(reservationPanel);

                    JOptionPane.showMessageDialog(
                            MotelReservationProgram.this,
                            messagePanel,
                            "예약 정보 확인",
                            JOptionPane.INFORMATION_MESSAGE);
                } 
                else 
                {
                    JOptionPane.showMessageDialog(
                            MotelReservationProgram.this,
                            "해당 객실에 예약된 정보가 없습니다.",
                            "예약 정보 확인",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        panel.add(checkReservationButton);
    }

    private void showReservationListButton(JPanel panel,String buttonType) {
    	JButton showReservationButton = null;

    	
        // "예약 목록 확인" 버튼 생성
    	if(buttonType == "roomType")
    	{
            showReservationButton = new JButton("객실별 예약 목록 확인");
    	}
    	else if(buttonType == "dateType")
    	{
            showReservationButton = new JButton("객실&날짜별 예약 목록 확인");
    	}
        showReservationButton.addActionListener(new ActionListener() {
            private List<ReservationInfo> reservations;
            private int currentIndex;
            private JFrame reservationWindow;

            public void actionPerformed(ActionEvent e) {
            	String searchStart = null;
            	String searchEnd = null; 
                // 호실 입력을 받는 창 생성
                AppendLog("예약목록 확인 버튼 클릭", Color.BLUE);
                String roomNumber = JOptionPane.showInputDialog(MotelReservationProgram.this, "호실을 입력하세요: ");
                
                if(buttonType == "dateType")
                {
                	searchStart = JOptionPane.showInputDialog(MotelReservationProgram.this, "검색시작 날짜를 입력하세요: ", LocalDate.now());
                	searchEnd = JOptionPane.showInputDialog(MotelReservationProgram.this, "검색종료 날짜를 입력하세요: ",  LocalDate.now().plusDays(1));
                }

            	if(buttonType == "roomType")
            	{
                    reservations = reservationDB.getReservationsByRoomNumber(roomNumber);
            	}
            	else if(buttonType == "dateType")
            	{
            	    LocalDate startDate = ParseDate(searchStart);
            	    LocalDate endDate = ParseDate(searchEnd);
                    reservations = reservationDB.getReservationsByRoomNumberAndDate(roomNumber, startDate, endDate);
            	}
                
                if (reservations.isEmpty()) {
                    JOptionPane.showMessageDialog(MotelReservationProgram.this, "해당 호실에 예약된 정보가 없습니다.", "예약 목록", JOptionPane.INFORMATION_MESSAGE);
                    AppendLog("예약목록 확인 실패", Color.BLUE);
                    return;
                }

                currentIndex = 0;

                // 예약 목록 출력
                showReservationInfo();
            }
            
            private void showReservationInfo() 
            {
                ReservationInfo reservation = reservations.get(currentIndex);

                // 예약 목록 텍스트 생성
                StringBuilder reservationText = new StringBuilder();
                reservationText.append("예약 번호: ").append(reservation.getRoomNumber()).append("\n");
                reservationText.append("이름: ").append(reservation.getName()).append("\n");
                reservationText.append("체크인 날짜: ").append(reservation.getCheckIn()).append("\n");
                reservationText.append("체크아웃 날짜: ").append(reservation.getCheckOut()).append("\n");
                reservationText.append("결제 수단: ").append(reservation.getPaymentMethod()).append("\n");
                reservationText.append("결제 금액: ").append(reservation.getPaymentAmount()).append("\n");
                reservationText.append("차량 번호: ").append(reservation.getCarNumber()).append("\n");
                reservationText.append("조식 인원: ").append(reservation.getBreakfastCount()).append("\n");
                reservationText.append("메모: ").append(reservation.getMemo()).append("\n");

				// 이전, 다음 버튼 생성
                JButton previousButton = new JButton("이전");
                JButton nextButton = new JButton("다음");

                previousButton.setEnabled(currentIndex > 0);
                nextButton.setEnabled(currentIndex < reservations.size() - 1);

                previousButton.addActionListener(new ActionListener() 
                {
                    public void actionPerformed(ActionEvent e) 
                    {
                        currentIndex--;
                        showReservationInfo();
                    }
                });

                nextButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        currentIndex++;
                        showReservationInfo();
                    }
                });

                JPanel buttonPanel = new JPanel();
                buttonPanel.add(previousButton);
                buttonPanel.add(nextButton);

                // 예약 목록 출력
                JTextArea reservationTextArea = new JTextArea(reservationText.toString());
                reservationTextArea.setEditable(false);
                
                // 글꼴 크기 설정
                Font font = new Font(reservationTextArea.getFont().getName(), Font.PLAIN, 20);
                reservationTextArea.setFont(font);

                JScrollPane scrollPane = new JScrollPane(reservationTextArea);

                JPanel messagePanel = new JPanel(new BorderLayout());
                messagePanel.add(scrollPane, BorderLayout.CENTER);
                messagePanel.add(buttonPanel, BorderLayout.SOUTH);
                // 기존의 예약 정보 확인 창 닫기
                if (reservationWindow != null) 
                {
                    reservationWindow.dispose();
                }

                // 새로운 예약 정보 확인 창 열기
                reservationWindow = new JFrame("예약 목록");
                reservationWindow.add(messagePanel);
                reservationWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                reservationWindow.pack();
                reservationWindow.setSize(600, 400); // 원하는 크기로 설정해주세요
                reservationWindow.setVisible(true);
            }
        });

        // "예약 목록 확인" 버튼을 infoPanel에 추가
        panel.add(showReservationButton);
    }
    
    private void createTodayParkingButton(JPanel panel) 
    {
        JButton todayParkingButton = new JButton("오늘 주차 정보");
        todayParkingButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                AppendLog("주차 정보 클릭", Color.BLACK);

                List<String> parkingInfoList = reservationDB.ShowTodayParkingInfo(selectDate);

                if (!parkingInfoList.isEmpty())
                {
                    StringBuilder message = new StringBuilder("주차정보:\n");

                    for (String parkingInfo : parkingInfoList) 
                    {
                        message.append(parkingInfo).append("\n\n");
                    }

                    JOptionPane.showMessageDialog(MotelReservationProgram.this, message.toString(), "오늘 주차 정보", JOptionPane.INFORMATION_MESSAGE);
                }
                else 
                {
                    JOptionPane.showMessageDialog(MotelReservationProgram.this, "오늘 주차한 정보가 없습니다.", "오늘 주차 정보", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        panel.add(todayParkingButton);
    }

    private void EditReservationInfo(JPanel panel, ReservationInfo reservationInfo)
    {
    	 // 버튼 클릭 로그
        AppendLog("예약 편집 - 객실 번호: " + reservationInfo.getRoomNumber(), Color.BLACK);

        panel = new JPanel(new GridLayout(9, 2)); // 입력 폼을 위한 그리드 레이아웃
        JLabel roomNumberLabel = new JLabel("객실 번호: " + reservationInfo.getRoomNumber()); // 객실 번호 표시
        JTextField nameField = new JTextField();
        JTextField checkInField = new JTextField();
        JTextField checkOutField = new JTextField();
        JTextField carNumberField = new JTextField();
        JTextField breakfastCountField = new JTextField();
        JTextField paymentMethodField = new JTextField();
        JTextField paymentAmountField = new JTextField();
        JTextField memoField = new JTextField();

        panel.add(roomNumberLabel);
        panel.add(new JLabel()); // 빈 레이블 추가하여 정렬 맞춤
        panel.add(new JLabel("이름:"));
        panel.add(nameField);
        nameField.setText(reservationInfo.getName());
        panel.add(new JLabel("체크인 날짜:"));
        panel.add(checkInField);
        checkInField.setText(reservationInfo.getCheckIn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        panel.add(new JLabel("체크아웃 날짜:"));
        panel.add(checkOutField);
        checkOutField.setText(reservationInfo.getCheckOut().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        panel.add(new JLabel("차량 번호:"));
        panel.add(carNumberField);
        carNumberField.setText(reservationInfo.getCarNumber());
        panel.add(new JLabel("조식 인원:"));
        panel.add(breakfastCountField);
        breakfastCountField.setText(Integer.toString(reservationInfo.getBreakfastCount()));
        panel.add(new JLabel("결제 수단:"));
        panel.add(paymentMethodField);
        paymentMethodField.setText(reservationInfo.getPaymentMethod());
        panel.add(new JLabel("결제 금액:"));
        panel.add(paymentAmountField);
        paymentAmountField.setText(Integer.toString(reservationInfo.getPaymentAmount()));
        panel.add(new JLabel("메모:"));
        panel.add(memoField);
        memoField.setText(reservationInfo.getMemo());

        int result = JOptionPane.showConfirmDialog(
                MotelReservationProgram.this,
                panel,
                "예약 정보 편집",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE );

        if (result == JOptionPane.OK_OPTION) 
        {
            String name = nameField.getText();
            String checkIn = checkInField.getText();
            String checkOut = checkOutField.getText();
            String carNumber = carNumberField.getText();
            int breakfastCount = Integer.parseInt(breakfastCountField.getText());
            String paymentMethod = paymentMethodField.getText();
            int paymentAmount = Integer.parseInt(paymentAmountField.getText());
            String memo = memoField.getText();

            // 예약 정보를 저장하거나 처리 String -> LocalDate
            LocalDate checkInDate = ParseDate(checkIn);
            LocalDate checkOutDate = ParseDate(checkOut);
            
            // 체크아웃 날짜가 체크인 날짜보다 빠르거나 같은 경우 에러
            if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate))
            {
                JOptionPane.showMessageDialog(
                    MotelReservationProgram.this,
                    "체크아웃 날짜가 잘못 되었습니다.",
                    "오류",
                    JOptionPane.ERROR_MESSAGE);
                
                return; // 예약 정보 저장 및 처리를 하지 않고 종료
            }

            ReservationInfo newReservationInfo = new ReservationInfo(
            		reservationInfo.getRoomNumber(),
            		name, 
            		checkInDate, 
            		checkOutDate, 
               		paymentMethod,
            		paymentAmount,
            		carNumber, 
            		breakfastCount, 
            		memo);
            
            // 중복예약 여부 확인
            if(reservationDB.isReservationDateValid(newReservationInfo) || 
            		(newReservationInfo.getCheckIn().isEqual(reservationInfo.getCheckIn()) && newReservationInfo.getCheckOut().isEqual(reservationInfo.getCheckOut())))
            {
                // DB에 예약정보 넣기
                reservationDB.SaveReservationToDatabase(newReservationInfo);
            }
            else
            {
                JOptionPane.showMessageDialog(
                        MotelReservationProgram.this,
                        "예약기간 내에 이미 예약이 존재합니다.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
                    
                	AppendLog("예약 정보 수정실패 (사유: 예약 기간 중복) - 객실 번호: " + newReservationInfo.getRoomNumber(), Color.RED);
                    return; // 예약 정보 저장 및 처리를 하지 않고 종료
            }

            // 변경점 업데이트 함수
            reservationDB.UpdateReservationInfo(reservationInfo, newReservationInfo);
            
            // 예약 정보 로그 출력
            AppendLog("예약 정보 편집완료 - 객실 번호: " + newReservationInfo.getRoomNumber(), Color.BLUE);
            AppendReservationLog(newReservationInfo, Color.BLUE);
         }
    }
    
    private void TickTimer()
    {
        int delay = 1000; // 1초마다 업데이트
         
        // 안에있는 작업들은 주기적으로 업데이트
        ActionListener taskPerformer = new ActionListener()
        {
            public void actionPerformed(ActionEvent evt) 
            {
                currentDateLabel.setText("현재 날짜: " + LocalDate.now());
                currentTimeLabel.setText("현재 시간: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                selectDateLabel.setText("기준 날짜: " + selectDate);
                checkOutTimeLabel.setText("체크아웃 시간: " + reservationDB.getCheckOutTime());  
            	totalAmountLabel.setText("오늘 총 매출: " + reservationDB.ShowTotalAmount(selectDate) + "원");  
            	totalBreakfastLabel.setText("조식 인원: " + reservationDB.ShowTotalBreakfast(selectDate) + "명");  
                
                // 숙박중인, 숙박예정인, 숙박했던 고객 정보 출력
                ShowGuestInfo(floor7Start, floor7End);
                ShowGuestInfo(floor8Start, floor8End);
                ShowGuestInfo(floor9Start, floor9End);
            }
        };
        

        
        timer = new Timer(delay, taskPerformer);
        timer.start();
    }
    
    public static void main(String[] args) 
    {
        new MotelReservationProgram();
    }
}
