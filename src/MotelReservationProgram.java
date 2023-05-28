import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import javax.swing.Timer;
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
    private Map<String, ReservationInfo> roomReservationMap;
    private JLabel currentDateLabel;
    private JLabel currentTimeLabel;
    private JTextPane logTextPane;
    private Timer timer;
    
    public MotelReservationProgram() 
    {
        setTitle("모텔 객실 예약 관리 프로그램");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 현재 날짜 레이블 생성
        currentDateLabel = new JLabel();
        currentTimeLabel = new JLabel();
        
        // 체크인 체크아웃 시간 설정
        LocalTime checkInTime = parseTime("13:00:00"); // 입실 시간
        LocalTime checkOutTime = parseTime("12:00:00"); // 퇴실 시간
        
        // 프레임을 전체화면으로 고정
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // 레이아웃 설정
        setLayout(new GridLayout(1, 2));
        
        // 좌측 패널
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(3, 1));

        // 각 층별로 패널 생성
        JPanel floor1Panel = createFloorPanel("7", 701, 711);
        JPanel floor2Panel = createFloorPanel("8", 801, 813);
        JPanel floor3Panel = createFloorPanel("9", 901, 913);
        

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
        infoPanel.add(currentDateLabel);
        infoPanel.add(currentTimeLabel);
        DateUpdateTimer();
        
        // log 패널 설정
        logTextPane = new JTextPane();
        logTextPane.setEditable(false); // 편집 불가능하도록 설정
        JScrollPane scrollPane = new JScrollPane(logTextPane);
        rightPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(leftPanel);
        add(rightPanel);

        roomReservationMap = new HashMap<>();

        setVisible(true);
    }

    private JPanel createFloorPanel(String floor, int startRoomNumber, int endRoomNumber) 
    {
        JPanel floorPanel = new JPanel();
        floorPanel.setLayout(new GridLayout(0, 7)); // 열 개수를 설정하여 다음 줄로 넘어가도록 함

        JLabel floorLabel = new JLabel(floor + "층");
        floorPanel.add(floorLabel);
        
        for (int i = startRoomNumber; i <= endRoomNumber; i++) 
        {
            String roomNumber = Integer.toString(i);
            
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();
        
        	
            JPanel roomPanel = new JPanel();
            roomPanel.setPreferredSize(new Dimension(130, 130)); // 조정된 높이
            roomPanel.setLayout(new BorderLayout());
            
            JLabel roomLabel = new JLabel();
            roomLabel.setText(Integer.toString(i) + "호");
            
            JLabel roomGuest = new JLabel();
                          
            JButton reserveButton = new JButton("예약");
            JButton checkReservationButton = new JButton("예약 확인"); // 추가된 버튼


            reserveButton.addActionListener(new ActionListener() 
            {
                public void actionPerformed(ActionEvent e) 
                {
                    // 버튼 클릭 로그
                    appendLog("예약 버튼 클릭 - 객실 번호: " + roomNumber, Color.BLACK);

                    JPanel panel = new JPanel(new GridLayout(8, 2)); // 입력 폼을 위한 그리드 레이아웃
                    JLabel roomNumberLabel = new JLabel("객실 번호: " + roomNumber); // 객실 번호 표시
                    JTextField nameField = new JTextField();
                    JTextField checkInField = new JTextField();
                    JTextField checkOutField = new JTextField();
                    JTextField carNumberField = new JTextField();
                    JTextField breakfastField = new JTextField();
                    JTextField paymentField = new JTextField();
                    JTextField memoField = new JTextField();

                    panel.add(roomNumberLabel);
                    panel.add(new JLabel()); // 빈 레이블 추가하여 정렬 맞춤
                    panel.add(new JLabel("이름:"));
                    panel.add(nameField);
                    panel.add(new JLabel("체크인 날짜:"));
                    panel.add(checkInField);
                    checkInField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))); // 현재 날짜 미리 입력
                    panel.add(new JLabel("체크아웃 날짜:"));
                    panel.add(checkOutField);
                    checkOutField.setText(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))); // 현재 날짜 + 1일 미리 입력
                    panel.add(new JLabel("차량 번호:"));
                    panel.add(carNumberField);
                    panel.add(new JLabel("조식 인원:"));
                    panel.add(breakfastField);
                    panel.add(new JLabel("결제 수단:"));
                    panel.add(paymentField);
                    panel.add(new JLabel("메모:"));
                    panel.add(memoField);

                    int result = JOptionPane.showConfirmDialog(
                            MotelReservationProgram.this,
                            panel,
                            "예약 정보 입력",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                            
                    );

                    if (result == JOptionPane.OK_OPTION) 
                    {
                        String name = nameField.getText();
                        String checkIn = checkInField.getText();
                        String checkOut = checkOutField.getText();
                        String carNumber = carNumberField.getText();
                        String breakfast = breakfastField.getText();
                        String payment = paymentField.getText();
                        String memo = memoField.getText();

                        // 예약 정보를 저장하거나 처리하는 로직 추가
                        LocalDate checkInDate = parseDate(checkIn);
                        LocalDate checkOutDate = parseDate(checkOut);
                        
                        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate))
                        {
                            JOptionPane.showMessageDialog(
                                MotelReservationProgram.this,
                                "체크아웃 날짜가 잘못 되었습니다.",
                                "오류",
                                JOptionPane.ERROR_MESSAGE
                            );
                            return; // 예약 정보 저장 및 처리를 하지 않고 종료
                        }

                        ReservationInfo reservationInfo = new ReservationInfo(checkInDate, 
                        		checkOutDate, 
                        		name, 
                        		carNumber, 
                        		breakfast, 
                        		payment, 
                        		memo);
                        
                        roomReservationMap.put(roomNumber, reservationInfo);
                        
                        // 예약 정보 로그 출력
                        appendLog("예약 완료 - 객실 번호: " + roomNumber, Color.BLUE);
                        appendReservationLog(reservationInfo, Color.BLUE);
                                            }
                }
            });

            checkReservationButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // 버튼 클릭 로그
                    appendLog("예약 확인 버튼 클릭 - 객실 번호: " + roomNumber, Color.BLACK);

                    ReservationInfo reservationInfo = roomReservationMap.get(roomNumber);

                    if (reservationInfo != null) {
                        // 예약 정보를 편집 불가능한 상태로 출력하여 확인하는 메시지 대화상자 표시
                        StringBuilder message = new StringBuilder();
                        message.append("객실 번호: ").append(roomNumber).append("\n");
                        message.append("이름: ").append(reservationInfo.getName()).append("\n");
                        message.append("체크인 날짜: ").append(reservationInfo.getCheckIn()).append("\n");
                        message.append("체크아웃 날짜: ").append(reservationInfo.getCheckOut()).append("\n");
                        message.append("차량 번호: ").append(reservationInfo.getCarNumber()).append("\n");
                        message.append("조식 인원: ").append(reservationInfo.getBreakfast()).append("\n");
                        message.append("결제 수단: ").append(reservationInfo.getPayment()).append("\n");
                        message.append("메모: ").append(reservationInfo.getMemo()).append("\n");

                        JTextArea textArea = new JTextArea(message.toString());
                        textArea.setEditable(false);

                        JButton deleteButton = new JButton("삭제");
                        deleteButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                // 버튼 클릭 로그
                                appendLog("예약 삭제 - 객실 번호: " + roomNumber, Color.RED);
                                appendReservationLog(reservationInfo, Color.RED);

                                roomReservationMap.remove(roomNumber);
                                JOptionPane.showMessageDialog(
                                        MotelReservationProgram.this,
                                        "예약 정보가 삭제되었습니다.",
                                        "예약 정보 삭제",
                                        JOptionPane.INFORMATION_MESSAGE
                                );

                                // 예약 정보 확인 창 닫기
                                Window window = SwingUtilities.getWindowAncestor(deleteButton);
                                window.dispose();
                                
                                // 예약정보가 삭제되면 텍스트 제거
                                if (roomReservationMap.isEmpty()) 
                                {
                                    roomGuest.setText(""); // roomGuest의 텍스트 제거
                                }
                                
                            }
                        });

                        JPanel messagePanel = new JPanel(new BorderLayout());
                        messagePanel.add(textArea, BorderLayout.CENTER);
                        messagePanel.add(deleteButton, BorderLayout.SOUTH);

                        JOptionPane.showMessageDialog(
                                MotelReservationProgram.this,
                                messagePanel,
                                "예약 정보 확인",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                    } else {
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
            roomPanel.add(roomGuest, BorderLayout.CENTER);
            
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


    
    private void appendLog(String message, Color color) 
    {
        // 로그 텍스트 스타일을 설정하기 위한 스타일 컬렉션 생성
        StyledDocument doc = logTextPane.getStyledDocument();
        Style style = logTextPane.addStyle("LogStyle", null);

        // 메시지에 해당하는 스타일을 적용
        StyleConstants.setForeground(style, color);

        try {
            // 현재 날짜 및 시간을 포맷팅하여 로그에 추가
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = formatter.format(new Date());

            // 로그 메시지와 날짜를 추가
            doc.insertString(doc.getLength(), "[" + formattedDate + "] " + message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
   
    private void appendReservationLog(ReservationInfo reservationInfo,Color color) 
    {
        appendLog("======================================", color);
        appendLog("예약자 명: " + reservationInfo.getName(), color);
        appendLog("체크인 날짜: " + reservationInfo.getCheckIn(), color);
        appendLog("체크아웃 날짜: " + reservationInfo.getCheckOut(), color);
        appendLog("차량번호: " + reservationInfo.getCarNumber(), color);
        appendLog("조식 인원: " + reservationInfo.getBreakfast(), color);
        appendLog("결제 수단: " + reservationInfo.getPayment(), color);
        appendLog("메모: " + reservationInfo.getMemo(), color);
        appendLog("======================================", color);
    }
    
    private LocalDate parseDate(String date) 
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return LocalDate.parse(date, formatter);
    }
    
    private LocalTime parseTime(String time) 
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalTime.parse(time, formatter);
    }
    
    private void DateUpdateTimer()
    {
        int delay = 1000; // 1초마다 업데이트
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) 
            {
                currentDateLabel.setText("현재 날짜: " + LocalDate.now());
                currentTimeLabel.setText("현재 시간: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
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
