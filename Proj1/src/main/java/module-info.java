module com.example.proj1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;

    opens com.example.proj1 to javafx.fxml;
    exports com.example.proj1;
}