module my.company.projetorotisseriejavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires javafx.graphics;
    requires java.desktop;
    requires javafx.base;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.bootstrapicons;

    opens my.company.projetorotisseriejavafx to javafx.fxml;
    opens my.company.projetorotisseriejavafx.Controller to javafx.fxml;
    opens my.company.projetorotisseriejavafx.Objects to javafx.base, javafx.fxml;
    
    exports my.company.projetorotisseriejavafx.Objects;
    exports my.company.projetorotisseriejavafx;
    opens my.company.projetorotisseriejavafx.Controller.Pane to javafx.fxml;
    opens my.company.projetorotisseriejavafx.Controller.Modal to javafx.fxml;
    exports my.company.projetorotisseriejavafx.Util;
    opens my.company.projetorotisseriejavafx.Util to javafx.base, javafx.fxml;

}
