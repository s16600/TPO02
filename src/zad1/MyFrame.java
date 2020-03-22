package zad1;

import static javafx.concurrent.Worker.State.FAILED;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import org.json.JSONException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

public class MyFrame extends JFrame {
	
	Service service;
	
	JTextField country = new JTextField();
	JTextField city = new JTextField();
	JTextField currency = new JTextField();
	
	JButton pobierz = new JButton("Pobierz dane");
	
	JLabel kursWaluty = new JLabel("Kurs waluty: ");
	JLabel kursZlotego = new JLabel("Kurs NBP: ");
	
	JTextPane pogoda = new JTextPane();

	//https://javastart.pl/baza-wiedzy/grafika_awt_swing/zarzadzanie_rozkladem
	
	//https://docs.oracle.com/javase/8/javafx/interoperability-tutorial/swing-fx-interoperability.htm#CHDIEEJE
	WebEngine engine;
	WebView view;
	final JFXPanel jfxPanel = new JFXPanel();
	
    private void createScene() {
   	 
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
 
            	view = new WebView();
                engine = view.getEngine();
                
                String url = "https://en.wikipedia.org/wiki/" + service.city;
                //System.out.println(url);
                view.getEngine().load(url);
 	 
              jfxPanel.setScene(new Scene(view));
            }
        });
    }
	
    public void loadMyURL(final String url) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String tmp1 = convertToURL(url);
 
                if (tmp1 == null) {
                    tmp1 = convertToURL("http://" + url);
                }
 
                engine.load(tmp1);
            }
        });
    }
    
    private static String convertToURL(String str) {
        try {
            return new URL(str).toExternalForm();
        } catch (MalformedURLException exception) {
            return null;
        }
    }
    
	public MyFrame(Service service) {
		super("Informacje miejskie");
		this.service = service;
		
		setSize(400, 400);
		setLocation(300,300);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(4, 4, 4, 4);
		
		
		country.setText(service.country);
		country.setPreferredSize(new Dimension(200,25));
	    gbc.gridx=0; gbc.gridy=0; gbc.gridwidth = 1; 
		panel.add(country,gbc);
		
		city.setText("Warsaw");
		city.setPreferredSize(new Dimension(200,25));
	    gbc.gridx=0; gbc.gridy=1; gbc.gridwidth = 1; 
		panel.add(city,gbc);
		
		currency.setText("USD");
		city.setPreferredSize(new Dimension(200,25));
	    gbc.gridx=0; gbc.gridy=2; gbc.gridwidth = 1; 
		panel.add(currency,gbc);
		
		gbc.gridx=1; gbc.gridy=0; gbc.gridwidth = 1; gbc.gridheight = 3;
		pobierz.setPreferredSize(new Dimension(150,90));
		panel.add(pobierz,gbc);
		
		gbc.gridx=0; gbc.gridy=3; gbc.gridwidth = 2; 
		panel.add(kursWaluty,gbc);
		
		gbc.gridx=0; gbc.gridy=6; gbc.gridwidth = 2;  
		panel.add(kursZlotego,gbc);
		
		gbc.gridx=0; gbc.gridy=9; gbc.gridwidth = 2;
		pogoda.setText("Pogoda:");
		pogoda.setPreferredSize(new Dimension(150,80));
		panel.add(pogoda,gbc);
		
		
		pobierz.addActionListener(
				e->{
					service.country=country.getText();
					service.city=city.getText();
					
					try {
						kursWaluty.setText("Kurs waluty: "+service.getRateFor(currency.getText()));
						kursZlotego.setText("Kurs NBP: "+service.getNBPRate());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					pogoda.setText("Pogoda:\n"+service.getWeather(city.getText()));
					
					loadMyURL("https://en.wikipedia.org/wiki/"+service.city);
					
					//loadURL("https://pl.wikipedia.org/wiki/"+service.city);
					//String url = "pl.wikipedia.org/wiki/"+"Warszawa";
					//loadURL(url);
					//loadURL("https://pl.wikipedia.org/wiki/"+"Warszawa");
					//loadURL("https://www.wp.pl");
				}
				);

		
		//JPanel panelPrzegladarki = new JPanel();
		//WebView przegladarka = new WebView();
        //WebEngine silnikStron = przegladarka.getEngine();
		
		/*
        gbc.gridx=0; gbc.gridy=12; gbc.gridwidth = 2; gbc.gridheight = 20;
        panelPrzegladarki.setPreferredSize(new Dimension(200,400));
        panelPrzegladarki.setBackground(Color.white);
		panel.add(panelPrzegladarki,gbc);
		*/
	
				
		gbc.gridx=0; gbc.gridy=12; gbc.gridwidth = 2; gbc.gridheight = 20;
		jfxPanel.setPreferredSize(new Dimension(200,400));
	    panel.add(jfxPanel, gbc);
	    
	    /*
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(jfxPanel);
            }
        });
        */
	    
	    createScene();
		
		add(panel);
		pack(); //!bez tego nie aktualizuje wyglądu
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
}