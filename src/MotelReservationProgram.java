import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import javax.swing.Timer;
import javax.swing.BoxLayout;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MotelReservationProgram extends JFrame
{
	private JLabel currentDateLabel;
	private JLabel currentTimeLabel;
	private JLabel selectDateLabel;
    
    LocalDate selectDate;
    public LocalTime checkOutTime;
    
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

        // 현재 날짜 레이블 생성
        currentDateLabel = new JLabel();
        currentTimeLabel = new JLabel();
        selectDateLabel = new JLabel();
        
        // 폰트 설정
        Font bigFont = new Font(Font.DIALOG, Font.PLAIN, 20);
        currentDateLabel.setFont(bigFont);
        currentTimeLabel.setFont(bigFont);
        selectDateLabel.setFont(bigFont);
        
        // 선택한 날짜 초기화
        selectDate = LocalDate.now();
        
        // 현재 날짜와 시간 초기화
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        
        // 체크인 체크아웃 시간 설정
        checkOutTime = ParseTime("12:00:00"); // 퇴실 시간
        
        // 프레임을 전체화면으로 고정
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // 레이아웃 설정
        setLayout(new GridLayout(1, 2));
        
        // 좌측 패널
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(3, 1));

        // 각 층별로 패널 생성
        JPanel floor1Panel = createFloorPanel("7", floor7Start, floor7End);
        JPanel floor2Panel = createFloorPanel("8", floor8Start, floor8End);
        JPanel floor3Panel = createFloorPanel("9", floor9Start, floor9End);
        

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
        rightPanel.add(infoPanel, BorderLayout.CENTER);
        infoPanel.add(Box.createVerticalStrut(100)); // 여백 설정
        infoPanel.add(currentDateLabel);
        infoPanel.add(currentTimeLabel);
        infoPanel.add(selectDateLabel);
        JButton setSelectDateButton = new JButton("기준날짜 변경");
        
        setSelectDateButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                // 버튼 클릭 로그
                AppendLog("기준날짜 설정버튼 클릭", Color.BLACK);

                JPanel panel = new JPanel(new GridLayout(1, 1)); // 입력 폼을 위한 그리드 레이아웃

                JTextField selectDateField = new JTextField();

                panel.add(new JLabel("기준 날짜:"));
                panel.add(selectDateField);
                selectDateField.setText(selectDate.format(DateTimeFormatter.ofPattern("YYYY.MM.dd"))); // 현재 날짜 미리 입력

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
                    AppendLog("기준날짜 변경 " + selectDate, Color.BLACK);
                 }
            }
        });
        
        infoPanel.add(setSelectDateButton); // info패널에 버튼 추가
        
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
        floorPanel.add(floorLabel);
        
        for (int i = startRoomNumber; i <= endRoomNumber; i++) 
        {
            String roomNumber = Integer.toString(i);
 
        	
            JPanel roomPanel = new JPanel();
            roomPanelMap.put(roomNumber, roomPanel); // roomPanel을 roomPanelMap에 추가
            roomPanel.setPreferredSize(new Dimension(130, 130)); // 조정된 높이
            roomPanel.setLayout(new BorderLayout());  
            
            JLabel roomLabel = new JLabel();
            roomLabelMap.put(roomNumber, roomLabel); // roomLabel을 roomLabelMap에 추가
            roomLabel.setText(Integer.toString(i) + "호"); 
            
            JLabel roomGuestLabel = new JLabel();
            roomGuestMap.put(roomNumber, roomGuestLabel); // roomLabel을 roomLabelMap에 추가
                          
            JButton reserveButton = new JButton("예약");
            JButton checkReservationButton = new JButton("예약 확인"); // 추가된 버튼


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
                    checkInField.setText(selectDate.format(DateTimeFormatter.ofPattern("YYYY.MM.dd"))); // 기준 날짜 미리 입력
                    panel.add(new JLabel("체크아웃 날짜:"));
                    panel.add(checkOutField);
                    checkOutField.setText(selectDate.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))); // 기준 날짜 + 1일 미리 입력
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
                        
                        // DB에 예약정보 넣기
                        reservationDB.SaveReservationToDatabase(reservationInfo);
                        
                        // 예약 정보 로그 출력
                        AppendLog("예약 완료 - 객실 번호: " + roomNumber, Color.BLUE);
                        AppendReservationLog(reservationInfo, Color.BLUE);
                     }
                }
            });

            checkReservationButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // 버튼 클릭 로그
                    AppendLog("예약 확인 버튼 클릭 - 객실 번호: " + roomNumber, Color.BLACK);

                    ReservationInfo reservationInfo = reservationDB.getReservationInfoByDateAndRoom(roomNumber, selectDate, checkOutTime);

                    if (reservationInfo != null) 
                    {
                    	JTextArea textArea = ShowReservationInfo(roomNumber, reservationInfo);

                        JButton deleteButton = new JButton("삭제");
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

                        JPanel messagePanel = new JPanel(new BorderLayout());
                        messagePanel.add(textArea, BorderLayout.CENTER);
                        messagePanel.add(deleteButton, BorderLayout.SOUTH);

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
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            });


            roomPanel.add(roomLabel, BorderLayout.NORTH);
            roomPanel.add(roomGuestLabel, BorderLayout.CENTER);
                        
            JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
            buttonPanel.add(reserveButton);
            buttonPanel.add(checkReservationButton); // 예약 확인 버튼 추가
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

        reservationDB.deleteReservationByDateAndRoom(roomNumber, selectDate);
        JOptionPane.showMessageDialog(
                MotelReservationProgram.this,
                "예약 정보가 삭제되었습니다.",
                "예약 정보 삭제",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private LocalDate ParseDate(String date) 
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return LocalDate.parse(date, formatter);
    }
    
    private LocalTime ParseTime(String time) 
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalTime.parse(time, formatter);
    }
    
    private void ShowGuestInfo(int floorStart, int floorEnd)
    {
    	ReservationInfo guestInfo = null;
    	
        for (int i = floorStart; i <= floorEnd; i++) 
        {
        	String roomNumber = Integer.toString(i);
        	guestInfo = reservationDB.getReservationInfoByDateAndRoom(roomNumber, selectDate, checkOutTime);      

            // roomGuest에 이름 넣기
            if (guestInfo != null) 
            {
            	// HTML 태그를 사용하여 줄 바꿈을 적용
            	roomGuestMap.get(roomNumber).setText("<html>" + 
            	guestInfo.getName() + 
            	"<br>" + 
            	"입실: " +
            	guestInfo.getCheckIn() + 
            	"<br>" + 
            	"퇴실: " +
            	guestInfo.getCheckOut() + 
            	"</html>");

            } 
            else 
            {
            	roomGuestMap.get(roomNumber).setText("");

            }
        	
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
