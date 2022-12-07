package com.hoffmann.icearenadesktop;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;


public class IceArenaController {
    private IceArenaDBManager dbManager = new IceArenaDBManager();
    private int buttonChosen = -1; // 0 - tickets/1 - workers/2 - clients/3 - reports
    private int tempID;
    private boolean enterform, isEdit;

    @FXML
    private Button button_close, submit_button, form_close, button_delete;
    @FXML
    private DialogPane enter_form;
    @FXML
    private TextField form_field_1, form_field_2, form_field_3, form_field_4, form_field_5;
    @FXML
    private ChoiceBox<String> chosebox_arenas;
    @FXML
    private TextField field_search;
    @FXML
    private ListView<String> list_content;
    @FXML
    private AnchorPane anchor_content;
    @FXML
    private Label top_label, enterform_label;

    @FXML
    void initialize() {
        chosebox_arenas.getItems().add("Area #1");
        chosebox_arenas.getItems().add("Area #2");
        chosebox_arenas.getItems().add("Area #3");
        anchor_content.setDisable(true);
        hideEnterForm();

        button_close.setOnAction(actionEvent -> {
            Platform.exit();
            System.exit(0);
        });

        chosebox_arenas.setOnAction(actionEvent -> {
            if(chosebox_arenas.getValue() == null) return;
            try {
                loadDataToList();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        });
    }

    @FXML
    void mouse_enter(MouseEvent event) {
        ((Button) event.getSource()).setStyle("-fx-background-radius: 10; -fx-background-color: #131112; -fx-border-color: #000000ff; -fx-border-radius: 10; " +
                "-fx-border-insets: -1; -fx-text-fill: #fff");
       }
    @FXML
    void mouse_exit(MouseEvent event) {
        ((Button) event.getSource()).setStyle("-fx-background-radius: 10; -fx-background-color: #fff; -fx-border-color: #000000ff; " +
                "-fx-border-radius: 10; -fx-border-insets: -1; -fx-text-fill: #000000ff");
    }

    @FXML
    void listSelected(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() >= 2) {
            String selected = (String) (((ListView) mouseEvent.getSource()).getSelectionModel().getSelectedItems().get(0));

            if(selected.startsWith("ID |")) return;
            if(selected == "Add new item...") {
                showEnterForm();
                return;
            }
            selected = selected.substring(1, selected.length()-1);
            switch (buttonChosen) { // 0 - tickets/1 - workers/2 - clients/3 - reports
                case 1, 2: {
                    String[] s = selected.split(" \\| ");
                    showEnterForm();
                    tempID = Integer.parseInt(s[0]);
                    form_field_1.setText(s[1].split(" ")[0]);
                    form_field_2.setText(s[1].split(" ")[1]);
                    form_field_5.setText(s[2]);
                    form_field_3.setText(s[3]);
                    form_field_4.setText(s[4]);
                    break;
                }
            }
            isEdit = true;

        }
    }

    @FXML
    void leftButtonsClicked(MouseEvent event) {
        list_content.getItems().clear();
        chosebox_arenas.getSelectionModel().clearSelection();
        top_label.setText(((Button) event.getSource()).getText());
        anchor_content.setDisable(false);

        switch(((Button) event.getSource()).getText()) {
            case "Tickets": {
                buttonChosen = 0;
                break;
            }
            case "Workers": {
                buttonChosen = 1;
                break;
            }
            case "Clients": {
                buttonChosen = 2;
                break;
            }
            case "Reports": {
                buttonChosen = 3;
                break;
            }
        }
    }

    @FXML
    void submitClicked(MouseEvent mouseEvent) throws SQLException {
        if(!checkEnterForm()) return;

        String query = "";

        if(isEdit) {
            try {
                switch (buttonChosen) {
                    case 1: {
                        query = "UPDATE workers SET " +
                                "name_lastname = '" + form_field_1.getText() + " " + form_field_2.getText() +
                                "', phone = '" + form_field_5.getText() +
                                "', salary = " + Integer.parseInt(form_field_3.getText()) +
                                ", role = '" + form_field_4.getText() +
                                "' WHERE worker_id = " + tempID + ";";
                        break;
                    }
                    case 2: {
                        query = "UPDATE clients SET " +
                                "name_surname = '" + form_field_1.getText() + " " + form_field_2.getText() +
                                "', phone = '" + form_field_5.getText() +
                                "', age = " + Integer.parseInt(form_field_3.getText()) +
                                ", discount = " +  Integer.parseInt(form_field_4.getText()) +
                                " WHERE client_id = " + tempID + ";";
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                clearForm();
                return;
            }
        } else {
            try {
                switch (buttonChosen) { // 0 - tickets/1 - workers/2 - clients/3 - reports
                    case 0: {
                        query = "INSERT INTO tickets(arena_id, client_id, date, ticket_type, price) VALUES(" +
                                Integer.parseInt(form_field_1.getText()) + ", " + Integer.parseInt(form_field_4.getText()) + ", '" +
                                form_field_2.getText() + "', '" + form_field_5.getText() + "', " + Integer.parseInt(form_field_3.getText()) + ");";

                        break;
                    }
                    case 1: {
                        int arenaID = Integer.parseInt(String.valueOf(chosebox_arenas.getValue().charAt(6)));
                        query = "INSERT INTO workers(arena_id, name_lastname, phone, salary, role) VALUES(" +
                                arenaID + ", '" + form_field_1.getText() + " " + form_field_2.getText() + "', '"
                                + form_field_5.getText() + "', " + Integer.parseInt(form_field_3.getText()) + ", '" + form_field_4.getText() + "');";
                        break;
                    }
                    case 2: {
                        query = "INSERT INTO clients(name_surname, phone, age, discount) VALUES('" +
                                form_field_1.getText() + " " + form_field_2.getText() + "', '" + form_field_5.getText() + "', " +
                                Integer.parseInt(form_field_3.getText()) + ", " + Integer.parseInt(form_field_4.getText()) + ");";
                        break;
                    }
                    case 3: {
                        query = "INSERT INTO reports(arena_id, client_id, date, price, commentar) VALUES(" +
                                Integer.parseInt(form_field_1.getText()) + ", " + Integer.parseInt(form_field_4.getText()) +
                                ", '" + form_field_2.getText() + "', " + Integer.parseInt(form_field_3.getText()) + ", '" +
                                form_field_5.getText() + "');";
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                clearForm();
                return;
            }
        }
        dbManager.execQuery(query);
        loadDataToList();
        hideEnterForm();
    }

    @FXML
    void onDeleteClicked(MouseEvent mouseEvent) throws SQLException {
        if(enterform) return;
        if(list_content.getSelectionModel().getSelectedItems().size() == 0) return;
        if(list_content.getSelectionModel().getSelectedItem().startsWith("Add new item")) return;
        if(list_content.getSelectionModel().getSelectedItem().startsWith("ID")) return;
        String query = "";
        int id = Integer.parseInt((list_content.getSelectionModel().getSelectedItem().split(" \\| "))[0].substring(1));
        switch (buttonChosen) { // 0 - tickets/1 - workers/2 - clients/3 - reports
            case 0: {
                query = "DELETE FROM tickets WHERE ticket_id = " + id + ";";
                break;
            }
            case 1: {
                query = "DELETE FROM workers WHERE worker_id = " + id + ";";
                break;
            }
            case 2: {
                query = "DELETE FROM clients WHERE client_id = " + id + ";";
                break;
            }
            case 3: {
                query = "DELETE FROM reports WHERE report_id = " + id + ";";
                break;
            }
        }
        dbManager.execQuery(query);
        loadDataToList();
    }

    @FXML
    void formCloseClicked(MouseEvent mouseEvent) {
        hideEnterForm();
    }

    @FXML
    void onSearchEntered(ActionEvent actionEvent) throws SQLException {
        if(list_content.getItems().size() == 0) return;
        if(field_search.getText().length() == 0) {
            loadDataToList();
            return;
        }
        String search = field_search.getText();
        loadDataToList();
        for (int i = 1; i < list_content.getItems().size(); i++) {
            if(!list_content.getItems().get(i).contains(search)) {
                list_content.getItems().remove(i);
            }
        }

    }


    private void loadDataToList() throws SQLException {
        int areaID = Integer.parseInt(String.valueOf(chosebox_arenas.getValue().charAt(6)));
        list_content.getItems().clear();
        String query = "";
        switch (buttonChosen) { // 0 - tickets/1 - workers/2 - clients/3 - reports
            case 0: {
                query = "SELECT * FROM tickets CROSS JOIN clients ON clients.client_id = tickets.client_id WHERE arena_id = " + areaID +";";
                list_content.getItems().add("ID | Client | Date | Ticket Type | Price ");
                break;
            }
            case 1: {
                query = "SELECT * FROM workers WHERE arena_id = " + areaID + ";";
                list_content.getItems().add("ID | Name Surname | Phone | Salary | Roles");
                break;
            }
            case 2: {
                query = "SELECT * FROM clients;";
                list_content.getItems().add("ID | Name Surname | Phone | Age | Discount % | Enters");
                break;
            }
            case 3: {
                query = "SELECT * FROM reports CROSS JOIN clients ON reports.client_id = clients.client_id WHERE arena_id = " + areaID +";";
                list_content.getItems().add("ID | Client | Date | Ticket Type | Commentar ");
                break;
            }
        }
        ResultSet result = dbManager.execQueryWithResult(query);

        while (result.next()) {
            String s = "";
            switch (buttonChosen) {
                case 0: { //tickets
                    s = "[" + result.getInt(1) + " | " + result.getString(8) + " | "
                            + result.getString(4) + " | " + result.getString(5) + " | " + result.getFloat(6) + "]";
                    break;
                }
                case 1: { //workers
                    s = "[" + result.getInt(1) + " | " + result.getString(3) + " | "
                            + result.getString(4)  + " | " +  result.getInt(5) + " | " + result.getString(6) + "]";
                    break;
                }
                case 2: { //clients
                    s = "[" + result.getInt(1) + " | " + result.getString(2) + " | " + result.getString(3) + " | " +
                            result.getInt(4) + " | " + result.getInt(5) + " | " + result.getInt(6) + "]";
                    break;
                }
                case 3: { //reports
                    s = "[" + result.getInt(1) + " | " + result.getString(8) + " | "
                            + result.getString(4) + " | " + result.getString(5) + " | " + result.getString(6) + "]";
                    break;
                }
            }
            list_content.getItems().add(s);
        }
        list_content.getItems().add("Add new item...");
    }

    private void showEnterForm() {
        enter_form.setDisable(false);
        submit_button.setDisable(false);
        form_field_1.setDisable(false);
        form_field_2.setDisable(false);
        form_field_3.setDisable(false);
        form_field_4.setDisable(false);
        form_field_5.setDisable(false);
        form_close.setDisable(false);

        normalizeForm();

        enter_form.setVisible(true);
        submit_button.setVisible(true);
        form_field_1.setVisible(true);
        form_field_2.setVisible(true);
        form_field_3.setVisible(true);
        form_field_4.setVisible(true);
        form_field_5.setVisible(true);
        enterform_label.setVisible(true);
        form_close.setVisible(true);

        enterform = true;
    }

    private void hideEnterForm() {
        clearForm();
        enter_form.setDisable(true);
        submit_button.setDisable(true);
        form_field_1.setDisable(true);
        form_field_2.setDisable(true);
        form_field_3.setDisable(true);
        form_field_4.setDisable(true);
        form_field_5.setDisable(true);
        form_close.setDisable(true);

        enter_form.setVisible(false);
        submit_button.setVisible(false);
        form_field_1.setVisible(false);
        form_field_2.setVisible(false);
        form_field_3.setVisible(false);
        form_field_4.setVisible(false);
        form_field_5.setVisible(false);
        enterform_label.setVisible(false);
        form_close.setVisible(false);

        isEdit = false;
        enterform = false;
    }

    private void clearForm() {
        form_field_1.setText("");
        form_field_2.setText("");
        form_field_3.setText("");
        form_field_4.setText("");
        form_field_5.setText("");
    }

    private void normalizeForm() {
        switch (buttonChosen) { // 0 - tickets/1 - workers/2 - clients/3 - reports
            case 0: {
                form_field_1.setPromptText("Arena ID");
                form_field_2.setPromptText("Date");
                form_field_3.setPromptText("Price");
                form_field_4.setPromptText("Client ID");
                form_field_5.setPromptText("Ticket type");
                break;
            }
            case 1: {
                form_field_1.setPromptText("Name");
                form_field_2.setPromptText("Surname");
                form_field_3.setPromptText("Salary");
                form_field_4.setPromptText("Role");
                form_field_5.setPromptText("Phone");
                break;
            }
            case 2: {
                form_field_1.setPromptText("Name");
                form_field_2.setPromptText("Surname");
                form_field_3.setPromptText("Age");
                form_field_4.setPromptText("Discount");
                form_field_5.setPromptText("Phone");
                break;
            }
            case 3: {
                form_field_1.setPromptText("Arena ID");
                form_field_2.setPromptText("Date");
                form_field_3.setPromptText("Price");
                form_field_4.setPromptText("Client ID");
                form_field_5.setPromptText("Commentar");
                break;
            }
        }
    }

    private boolean checkEnterForm() {
        return form_field_1.getText().length() > 0 && form_field_1.getText().length() > 0
                && form_field_1.getText().length() > 0 && form_field_1.getText().length() > 0 && form_field_1.getText().length() > 0;
    }

}
