<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
   GAME TIC TAC TOE (CARO 3x3)
</h2>
<div align="center">
    <p align="center">
        <img src="images/aiotlab_logo.png" alt="AIoTLab Logo" width="170"/>
        <img src="images/fitdnu_logo.png" alt="AIoTLab Logo" width="180"/>
        <img src="images/dnu_logo.png" alt="DaiNam University Logo" width="200"/>
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)

</div>


## 1. Giới thiệu hệ thống

Hệ thống Game Tic Tac Toe được phát triển theo mô hình Client – Server:

- Client: ứng dụng giao diện đồ họa bằng Java Swing để người chơi thao tác.
- Server: quản lý kết nối, ghép cặp người chơi, xử lý luật chơi và lưu trữ lịch sử trận đấu.

** Chức năng chính **
- Đăng nhập/nhập tên để tham gia hệ thống.
- Ghép cặp tự động giữa hai người chơi.
- Chơi Tic Tac Toe online với luật chuẩn (X/O, thắng, thua, hòa).
- Xử lý tình huống thoát giữa chừng (người còn lại thắng).
- Lưu lịch sử trận đấu (người lại.
- 
## 2. Công nghệ sử dụng
[![Java](https://img.shields.io/badge/Java%20JDK-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![Swing](https://img.shields.io/badge/Java%20Swing-5382a1?style=for-the-badge&logo=coffeescript&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Socket](https://img.shields.io/badge/TCP%20Socket-0066cc?style=for-the-badge&logo=socketdotio&logoColor=white)](https://en.wikipedia.org/wiki/Network_socket)
[![Architecture](https://img.shields.io/badge/Architecture-Server%20%2B%20Client-6db33f?style=for-the-badge&logo=databricks&logoColor=white)](#)

## 3. Hình ảnh các chức năng
- Màn hình đăng nhập/kết nối
    - Nhập Tên người chơi.
    - Nút Kết nối.
    <p align="center">
     <img src="images/nhapten.png" alt="Ảnh 1" width="500"/>
    </p>

    <p align="center">
    <em>Hình 1: Nhập tên sau khi chạy client</em>
    </p>

- Màn hình chơi game
    - Bàn cờ 3x3 bằng các nút bấm.
    - Hiển thị lượt chơi.
    - Khi có kết quả, hệ thống thông báo thắng/thua/hòa.
      
    <p align="center">
      <img src="images/choi.png" alt="Ảnh 2" width="500"/>
    </p>
    <p align="center">
    
      <em>Hình 2: Ghép phòng 2 người chơi</em>
    </p>

- Thông báo kết quả
    - Cửa sổ popup hiển thị kết quả trận đấu.
      
      <p align="center">
      <img src="images/kq.png" alt="Ảnh 3" width="500"/>
        </p>
        
    <p align="center">
      <em> Hình 3: Kêt quả trận đấu </em>
    </p>
    
- Xem lịch sử trận đấu
    
    <p align="center">
  <img src="images/lsd.png" alt="Ảnh 4" width="500"/>
    </p>

    <p align="center">
      <em> Hình 4: Lịch sử đấu </em>
    </p>

## 4. Cài đặt & chạy chương trình
- Bước 1: Chuẩn bị môi trường
    - Cài đặt Java JDK 8+.
    - Kiểm tra bằng: java -version

- Bước 2: Biên dịch chương trình
    - javac server/*.java
    - javac client/*.java

- Bước 3: Khởi chạy Server
    - java server.ServerMain

- Bước 4: Khởi chạy Client
    - Mở 2 terminal khác nhau để mô phỏng 2 người chơi, mỗi bên chạy:
    - java client.LoginFrame
    - Nhập tên người chơi khác nhau.

- Bước 5: Chơi game
    - Hai client kết nối thành công sẽ được ghép cặp.
    - Người chơi lần lượt đánh X/O trên giao diện.
    - Khi kết thúc, kết quả hiển thị và lưu lại lịch sử đấu.

## Thông tin liên hệ  
Họ tên: Nguyễn Thị Kiều Hoa.  
Lớp: CNTT 16-03.  
Email: ngochoa061204@gmail.com.

© 2025 AIoTLab, Faculty of Information Technology, DaiNam University. All rights reserved.

---

