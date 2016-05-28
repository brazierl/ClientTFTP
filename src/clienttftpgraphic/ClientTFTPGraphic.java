/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienttftpgraphic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author p1509019
 */
public class ClientTFTPGraphic extends Application {

    private String localFilePath1;
    private String localFilePath2;
    private String adresse;
    private int port;

    private Label lblNomFic;
    private Label lblNomDoss;
    private Label lblResultat1;
    private Label lblResultat2;

    @Override
    public void start(Stage primaryStage) {

        TabPane tabPane = new TabPane();
        Tab tabEnvoi = new Tab();
        Tab tabRecep = new Tab();

        final FileChooser fileChooser1 = new FileChooser();
        fileChooser1.setTitle("Sélection d'un fichier à envoyer");
        final Button openButton1 = new Button("Ouvrir un fichier...");

        openButton1.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File file = fileChooser1.showOpenDialog(primaryStage);
                if (file != null) {
                    openFile(file);
                }
            }
        });

        // Grille envoi
        GridPane gridEnvoi = new GridPane();
        gridEnvoi.setPadding(new Insets(10, 10, 10, 10));
        gridEnvoi.setVgap(5);
        gridEnvoi.setHgap(5);
        Label lblAddr1 = new Label("Adresse");
        TextField tFAddr1 = new TextField();
        tFAddr1.setPromptText("Entrer l'adresse...");
        GridPane.setConstraints(lblAddr1, 0, 0);
        GridPane.setConstraints(tFAddr1, 1, 0);
        gridEnvoi.getChildren().add(lblAddr1);
        gridEnvoi.getChildren().add(tFAddr1);

        Label lblPort1 = new Label("Port");
        TextField tFPort1 = new TextField();
        tFPort1.setPromptText("Entrer le port...");
        GridPane.setConstraints(lblPort1, 0, 1);
        GridPane.setConstraints(tFPort1, 1, 1);
        gridEnvoi.getChildren().add(lblPort1);
        gridEnvoi.getChildren().add(tFPort1);

        Label lblFicDist1 = new Label("Nom Fichier distant");
        TextField tFFicDist1 = new TextField();
        tFFicDist1.setPromptText("Entrer le nom...");
        GridPane.setConstraints(lblFicDist1, 0, 2);
        GridPane.setConstraints(tFFicDist1, 1, 2);
        gridEnvoi.getChildren().add(lblFicDist1);
        gridEnvoi.getChildren().add(tFFicDist1);

        lblNomFic = new Label("Aucun fichier...");
        GridPane.setConstraints(openButton1, 0, 3);
        GridPane.setConstraints(lblNomFic, 1, 3);
        gridEnvoi.getChildren().add(openButton1);
        gridEnvoi.getChildren().add(lblNomFic);

        final Button sendButton = new Button("Envoyer");

        lblResultat1 = new Label();

        sendButton.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                if (localFilePath1 != null) {
                    int res = sendFile(tFFicDist1.getText(), localFilePath1, tFAddr1.getText(), Integer.parseInt(tFPort1.getText()));
                    if (res == 0) {
                        displayResult(lblResultat1, "Transfert effectué.", false);
                    } else if (res > 0) {
                        displayResult(lblResultat1, "Erreur de communication avec le serveur.", true);
                    } else if (res < 0) {
                        displayResult(lblResultat1, "Erreur local de transfert.", true);
                    }

                } else {
                    displayResult(lblResultat1, "Selectionner un fichier.", true);
                }
            }
        });

        GridPane.setConstraints(sendButton, 0, 4);
        GridPane.setConstraints(lblResultat1, 1, 4);
        gridEnvoi.getChildren().add(sendButton);
        gridEnvoi.getChildren().add(lblResultat1);

        // Grille reception
        GridPane gridRecep = new GridPane();
        gridRecep.setPadding(new Insets(10, 10, 10, 10));
        gridRecep.setVgap(5);
        gridRecep.setHgap(5);
        Label lblAddr2 = new Label("Adresse");
        TextField tFAddr2 = new TextField();
        tFAddr2.setPromptText("Entrer l'adresse...");
        GridPane.setConstraints(lblAddr2, 0, 0);
        GridPane.setConstraints(tFAddr2, 1, 0);
        gridRecep.getChildren().add(lblAddr2);
        gridRecep.getChildren().add(tFAddr2);

        Label lblPort2 = new Label("Port");
        TextField tFPort2 = new TextField();
        tFPort2.setPromptText("Entrer le port...");
        GridPane.setConstraints(lblPort2, 0, 1);
        GridPane.setConstraints(tFPort2, 1, 1);
        gridRecep.getChildren().add(lblPort2);
        gridRecep.getChildren().add(tFPort2);

        Label lblFicDist2 = new Label("Nom Fichier distant");
        TextField tFFicDist2 = new TextField();
        tFFicDist2.setPromptText("Entrer le nom...");
        GridPane.setConstraints(lblFicDist2, 0, 2);
        GridPane.setConstraints(tFFicDist2, 1, 2);
        gridRecep.getChildren().add(lblFicDist2);
        gridRecep.getChildren().add(tFFicDist2);

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Sélection d'un dossier de réception");
        final Button openButton2 = new Button("Ouvrir un dossier...");

        openButton2.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File file = directoryChooser.showDialog(primaryStage);
                if (file != null) {
                    openDir(file);
                }
            }
        });

        lblNomDoss = new Label("Aucun dossier...");
        GridPane.setConstraints(openButton2, 0, 3);
        GridPane.setConstraints(lblNomDoss, 1, 3);
        gridRecep.getChildren().add(openButton2);
        gridRecep.getChildren().add(lblNomDoss);
        
        Label lblFicLocal2 = new Label("Nom Fichier local");
        TextField tFFicLocal2 = new TextField();
        tFFicLocal2.setPromptText("Entrer le nom...");
        GridPane.setConstraints(lblFicLocal2, 0, 4);
        GridPane.setConstraints(tFFicLocal2, 1, 4);
        gridRecep.getChildren().add(lblFicLocal2);
        gridRecep.getChildren().add(tFFicLocal2);

        final Button receiveButton = new Button("Recevoir");

        lblResultat2 = new Label();

        receiveButton.setOnAction(
                new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                if (localFilePath2 != null && localFilePath2 != null ) {
                    int res = receiveFile(tFFicDist2.getText(), localFilePath2+"\\"+tFFicLocal2.getText(), tFAddr2.getText(), Integer.parseInt(tFPort2.getText()));
                    if (res == 0) {
                        displayResult(lblResultat2, "Transfert effectué.", false);
                    } else if (res > 0) {
                        displayResult(lblResultat2, "Erreur de communication avec le serveur.", true);
                    } else if (res < 0) {
                        displayResult(lblResultat2, "Erreur local de transfert.", true);
                    }

                } else {
                    displayResult(lblResultat2, "Selectionner un dossier.", true);
                }
            }
        });

        GridPane.setConstraints(receiveButton, 0, 5);
        GridPane.setConstraints(lblResultat2, 1, 5);
        gridRecep.getChildren().add(receiveButton);
        gridRecep.getChildren().add(lblResultat2);

        tabEnvoi.setContent(gridEnvoi);
        tabEnvoi.setClosable(false);
        tabEnvoi.setText("Envoi");

        tabRecep.setContent(gridRecep);
        tabRecep.setClosable(false);
        tabRecep.setText("Reception");

        tabPane.getTabs().add(tabEnvoi);
        tabPane.getTabs().add(tabRecep);

        StackPane root = new StackPane();
        root.getChildren().add(tabPane);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("Client TFTP");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openFile(File file) {
        localFilePath1 = file.getAbsolutePath();
        lblNomFic.setText(file.getName());
    }

    private void openDir(File file) {
        localFilePath2 = file.getAbsolutePath();
        lblNomDoss.setText(file.getAbsolutePath());
    }

    private void displayResult(Label lblResultat1, String msg, boolean erreur) {
        lblResultat1.setText(msg);
        if (erreur) {
            lblResultat1.setStyle("-fx-text-fill:red;");
        } else {
            lblResultat1.setStyle("-fx-text-fill:green;");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static int sendFile(String nomDistant, String cheminLocal, String ip, int port) {
        DatagramSocket ds;
        try {
            ds = new DatagramSocket();
            InetAddress adr;
            adr = InetAddress.getByName(ip);
            byte[] data;
            String msg = "\0\2" + nomDistant + "\0OCTET\0";
            data = msg.getBytes("ASCII");
            DatagramPacket wrq = new DatagramPacket(data, data.length, adr, port);
            ds.send(wrq);
            DatagramPacket ack = new DatagramPacket(data, data.length);
            ds.receive(ack);
            port = ack.getPort();

            // Récupère 512 bytes du fichier fic à partir du start'ième byte, et les placent dans le champ datas de dp
            int dataLength = 0;
            int maxSize = 512;
            int idBloc = 1;
            try {
                FileInputStream fic = new FileInputStream(cheminLocal);
                do {
                    data = new byte[maxSize + 4];
                    int b;
                    int i;
                    int iFic;
                    byte[] code = ByteBuffer.allocate(4).putInt(3).array();
                    data[0] = code[2];
                    data[1] = code[3];
                    byte[] bloc = ByteBuffer.allocate(4).putInt(idBloc).array();
                    data[2] = bloc[2];
                    data[3] = bloc[3];
                    i = 4;

                    for (iFic = 0; iFic < maxSize; iFic++) {
                        b = fic.read();
                        if (b == -1) {
                            dataLength = -1;
                            break;
                        } else {
                            data[i] = (byte) b;
                            dataLength = data.length;
                            i++;
                        }
                    }
                    if (dataLength == -1) {
                        byte[] tmp = data;
                        data = Arrays.copyOf(tmp, i);
                    }
                    DatagramPacket dp = new DatagramPacket(data, data.length, adr, port);
                    ds.send(dp);
                    ds.receive(dp);
                    idBloc++;
                } while (maxSize + 4 == dataLength && dataLength > 0);
                fic.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientTFTPGraphic.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
            System.out.println("Transfert réussi !");
            return 0;
        } catch (SocketException | UnknownHostException | UnsupportedEncodingException ex) {
            Logger.getLogger(ClientTFTPGraphic.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        } catch (IOException ex) {
            Logger.getLogger(ClientTFTPGraphic.class.getName()).log(Level.SEVERE, null, ex);
            return -1;//Erreur locale : Chemin invalide
        }

    }

    public int receiveFile(String nomDistant, String cheminLocal, String ip, int port) {
        DatagramSocket ds;
        try {
            ds = new DatagramSocket();
            InetAddress adr;
            adr = InetAddress.getByName(ip);
            byte[] byteMsg;
            String msg = "\0\1" + nomDistant + "\0OCTET\0";
            byteMsg = msg.getBytes("ASCII");
            DatagramPacket rrq = new DatagramPacket(byteMsg, byteMsg.length, adr, port);
            ds.send(rrq);
            DatagramPacket data = new DatagramPacket(byteMsg, byteMsg.length);

            int idBloc = 1;
            int maxSize = 512;
            int dataLength = 0;

            File destFic = new File(cheminLocal);
            destFic.createNewFile();
            FileOutputStream fic = new FileOutputStream(destFic);

            byteMsg = new byte[516];
            data = new DatagramPacket(byteMsg, byteMsg.length);
            do {
                ds.receive(data);
                dataLength = data.getLength();
                port = data.getPort();
                if(data.getData()[1]!=5)
                    fic.write(Arrays.copyOfRange(data.getData(), 4, data.getData().length));
                else{
                    displayResult(lblResultat2, new String(data.getData()), true);
                    return 1;
                }

                byteMsg = new byte[516];

                byte[] code = ByteBuffer.allocate(4).putInt(4).array();
                byteMsg[0] = code[2];
                byteMsg[1] = code[3];
                byte[] bloc = ByteBuffer.allocate(4).putInt(idBloc).array();
                byteMsg[2] = bloc[2];
                byteMsg[3] = bloc[3];

                DatagramPacket ack = new DatagramPacket(byteMsg, byteMsg.length, adr, port);
                ds.send(ack);

                idBloc++;
            } while (dataLength == maxSize + 4);
            fic.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientTFTPGraphic.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        System.out.println("Transfert réussi !");
        return 0;
    }
}
