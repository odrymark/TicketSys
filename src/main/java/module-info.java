module dk.easv.ticketsys {
    requires javafx.controls;
    requires javafx.fxml;


    opens dk.easv.ticketsys to javafx.fxml;
    exports dk.easv.ticketsys;
    exports dk.easv.ticketsys.PL;
    opens dk.easv.ticketsys.PL to javafx.fxml;
}