module com.example.gamefinal {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gamefinal to javafx.fxml;
    exports com.example.gamefinal;
}