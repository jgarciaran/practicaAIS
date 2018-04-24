package Buscaminas;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Buscaminas extends JFrame implements ActionListener, MouseListener, Serializable{ 
   //Creacion de atributos
        //Atributos del código original
    int nomines;
    int perm[][];
    String tmp;
    boolean found = false;
    int row;
    int column;
    int guesses[][];
    JButton b[][];
    int[][] mines;
    boolean allmines;
    int n, m;
    int deltax[] = {-1, 0, 1, -1, 1, -1, 0, 1};
    int deltay[] = {-1, -1, -1, 0, 0, 1, 1, 1};
    //Atributos añadidos
    int mostrartiempo;//Variable que mostrará el tiempo en la pantalla de juego(Contará segundo a segundo).
    int newmines;//Variable que mostrará el número de minas restantes.
    String nivel; //Variable String que indica que nivel de dificultad se ha elegido.
    Timer timer;//Timer que controla la variable mostrartiempo
    TimerTask ttask;//TimerTaks que indicará como funcionará el timer.
    JFrame frame;//Pantalla
    JMenuBar menumb;//Barra del menú
    JMenu menu1;//Menú dsplegable 
    JMenuItem reiniciar,nuevoJuego,guardar,cargar; //Opciones del menú
    JLabel minas,tiempo; // Etiquetas en las que se mostrarán las variables mostrartiempo y newmines.

    public Buscaminas(int n1, int m1, int num, String lvl){ //Primer constructor básico del buscaminas, es el que se llama al crear un juego nuevo.
       //Inicialización de los atributos.
        this.n = n1;
        this.m = m1;
        this.nomines = num; 
        this.nivel=lvl; // 
        mostrartiempo=-1;
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        perm = new int[n][m];
        boolean allmines = false;
        guesses = new int [n+2][m+2];
        mines = new int[n+2][m+2];
        b = new JButton [n][m];
         newmines=nomines;//En un juego nuevo la variable que muestra las minas empieza con el numero de minas total.
        //Creación de la pantalla
      frame= new JFrame("BUSCAMINAS");
      frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
      menumb = new JMenuBar();
      menu1= new JMenu("Opciones");
      reiniciar= new JMenuItem("Reiniciar");
      nuevoJuego = new JMenuItem("Nuevo Juego");
      guardar = new JMenuItem("Guardar Partida");
     cargar = new JMenuItem("Cargar Partida");
      
     //creacion de las etiquetas que muestran minas y tiempo a tiempo real.
      minas=new JLabel("Minas:"+newmines+" ");
      tiempo=new JLabel("Tiempo:"+mostrartiempo);
      //Creacion del timer y su timertask que ira sumando de uno en uno cada segundo y que servirá de cronómetro.
      timer=new Timer();
      ttask = new TimerTask(){
            @Override
            public void run() {
                mostrartiempo++;//se suma uno
                tiempo.setText("Tiempo:"+mostrartiempo);//cada vez que se suma se actualiza la etiqueta
            }
          
      };
     
       timer.schedule(ttask, 0,1000);//Se realiza el timerTask cada segundo
       
      
     
     //ActionListener de la opcion reiniciar del menu:Elimina el frame y vuelve a llamar a un nuevo buscaminas con las mismas opciones básicas.
     reiniciar.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            frame.dispose();
            new Buscaminas(n,m,nomines,nivel);
            
        }
     });
     //ActionListener de la opcion cargar del menu:Coge el fichero de la ultima partida guardada y lo carga en un nuevo objeto Buscaminas que utiliza otro constructor para juegos ya empezados.
     cargar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                //Nuevas variables
                int n,m,nomines,newmines,row,column,mostrartiempo;
                int[][] perm,guesses,mines;
                String nivel,tmp;
                JButton [][] b;
                boolean found;
                   ObjectInputStream oos = null;
                try {
                    //Apertura de fichero
                   oos = new ObjectInputStream(new FileInputStream("partida.txt"));
                    
                    //Lectura en orden de los elementos
                    n= oos.readInt();
                    m=oos.readInt();
                    nomines=oos.readInt();
                    nivel = (String)oos.readObject();
                    newmines=oos.readInt();
                    perm=(int[][]) oos.readObject();
                    tmp= (String) oos.readObject();
                    found= oos.readBoolean();
                    row= oos.readInt();
                    column= oos.readInt();
                    guesses= (int [][]) oos.readObject();
                    mines=(int[][]) oos.readObject();
                    mostrartiempo=oos.readInt();

                    //recogida de los valores de los botones y creacion de la nueva variable "b" que se crea nueva con los valores de los string metidos en guardar
                    b = new JButton[n][m];

                        for(int i=0;i<n;i++){
                            for(int j=0;j<m;j++){
                             b[i][j] =(new JButton((String) oos.readObject()));
                           
                            }
                        }
                    //Eliminación del frame actual y llamada a nuevo Buscaminas con el constructor 2.
                    frame.dispose();
                    new Buscaminas(n,m,nomines,nivel,newmines,perm,tmp,found,row,column,guesses,b,mines,mostrartiempo);
                    
                   
                    
                    } catch (FileNotFoundException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
     //Opcion Nuevo juego del menú: carga de nuevo el Menú Opciones que muestra el menu principal donde se eligen los niveles.
     nuevoJuego.addActionListener(new ActionListener() {
 	        public void actionPerformed(ActionEvent ev) {
 	            frame.dispose();
 	            Opciones();
 	    }
    });
     //Opcion guardar del menú, guarda la partida actual en un fichero de texto.
     guardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {

                ObjectOutputStream oos = null;
                try {
                    //apertura del fichero
                   oos = new ObjectOutputStream(new FileOutputStream("partida.txt"));
                    //escritura de los elementos de la partida actual en orden.
                   oos.writeInt(n);
                    oos.writeInt(m);
                   oos.writeInt(nomines);
                    oos.writeObject(nivel);
                    oos.writeInt(newmines);
                    oos.writeObject(perm);
                    oos.writeObject(tmp);
                    oos.writeBoolean(found);
                   oos.writeInt(row);
                    oos.writeInt(column);
                    oos.writeObject(guesses);
                  
                    oos.writeObject(mines);
                    oos.writeInt(mostrartiempo);
                    //tenia un fallo al pasar los botones al fichero, he pasado manualmente los valores en un string que luego se recogen en la lectura
                    for (JButton[] boton : b){
                        for (JButton boton2 : boton){
                            oos.writeObject(boton2.getText());
                            
                           
                           
                        }
                    }
                   
               
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        oos.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
             
        });
     //Creación del menú
      menumb.add(menu1);
      menu1.add(reiniciar);
      menu1.add(nuevoJuego);
      frame.setJMenuBar(menumb);
      menumb.add(minas);
       menumb.add(tiempo);
       menu1.add(guardar);
       menu1.add(cargar);
       
       //Creación del fondo de la pantalla y de las variables originales
       
        frame.setLayout(new GridLayout(n,m));
        
        for (int y = 0;y<m+2;y++){
            mines[0][y] = 3;
            mines[n+1][y] = 3;
            guesses[0][y] = 3;
            guesses[n+1][y] = 3;
        }
        for (int x = 0;x<n+2;x++){
            mines[x][0] = 3;
            mines[x][m+1] = 3;
            guesses[x][0] = 3;
            guesses[x][m+1] = 3;
        }
        do {
            int check = 0;
            for (int y = 1;y<m+1;y++){
                for (int x = 1;x<n+1;x++){
                    mines[x][y] = 0;
                    guesses[x][y] = 0;
                }
            }
            for (int x = 0;x<nomines;x++){
                mines [(int) (Math.random()*(n)+1)][(int) (Math.random()*(m)+1)] = 1;
            }
            for (int x = 0;x<n;x++){
                for (int y = 0;y<m;y++){
                if (mines[x+1][y+1] == 1){
                        check++;
                    }
                }
            }
            if (check == nomines){
                allmines = true;
            }
        }while (allmines == false);
        for (int y = 0;y<m;y++){
            for (int x = 0;x<n;x++){
                if ((mines[x+1][y+1] == 0) || (mines[x+1][y+1] == 1)){
                    perm[x][y] = perimcheck(x,y);
                }
                b[x][y] = new JButton("?");
               
                b[x][y].addActionListener(this);
                b[x][y].addMouseListener(this);
                
               frame.add(b[x][y]);
               
                b[x][y].setEnabled(true);
                
            }//end inner for
        }//end for
        //activacion del frame
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        
        for (int y = 0;y<m+2;y++){
            for (int x = 0;x<n+2;x++){
                System.out.print(mines[x][y]);
            }
           
        System.out.println("");}
      
    }//end constructor Mine()
    //Constructor 2 de la clase Buscaminas que carga un Buscaminas a mitad de partida.
 public Buscaminas(int n1, int m1, int num, String lvl,int newmines, int[][] perm,String tmp, boolean found, int row, int column, int guesses[][],JButton b[][],int[][] mines,int mt){
       //Inicializacion de variables del constructor
        this.n = n1;
        this.m = m1;
        this.nomines = num;
        this.nivel=lvl;
        this.newmines=newmines;
        this.perm=perm;
        this.tmp=tmp;
        this.mostrartiempo=mt-1;
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        boolean allmines = false;
        this.guesses = guesses;
        this.mines = mines;
        this.b=b;
        
        //Creacion del frame como en el constructor 1.
      frame= new JFrame("BUSCAMINAS");
      frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
      menumb = new JMenuBar();
      menu1= new JMenu("Opciones");
      reiniciar= new JMenuItem("Reiniciar");
      nuevoJuego = new JMenuItem("Nuevo Juego");
      guardar = new JMenuItem("Guardar Partida");
      cargar = new JMenuItem("Cargar Partida");
      
      //Creacion de las Jlabel como en el constructor1.
      minas=new JLabel("Minas:"+newmines+" ");
      tiempo=new JLabel("Tiempo:"+mostrartiempo);
      //Se usa el mismo timer y timertask que en el constructor1.
      timer=new Timer();
      ttask = new TimerTask(){
            @Override
            public void run() {
               mostrartiempo++;
                tiempo.setText("Tiempo:"+mostrartiempo);
            }
          
      };
     
       timer.schedule(ttask, 0,1000);
       
      
     
     //Opcion reiniciar hace lo mismo en ambos constructores
     reiniciar.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            frame.dispose();
            new Buscaminas(n,m,nomines,nivel);
            
    }
        });
     //Opcion cargar hace lo mismo en ambos constructores
     cargar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int n,m,nomines,newmines,row,column,mostrartiempo;
                int[][] perm,guesses,mines;
                String nivel,tmp;
                JButton [][] b;
                boolean found;
                   ObjectInputStream oos = null;
                try {
                   oos = new ObjectInputStream(new FileInputStream("partida.txt"));
                    
                    
                    n= oos.readInt();
                    m=oos.readInt();
                    nomines=oos.readInt();
                    nivel = (String)oos.readObject();
                    newmines=oos.readInt();
                    perm=(int[][]) oos.readObject();
                    tmp= (String) oos.readObject();
                    found= oos.readBoolean();
                    row= oos.readInt();
                    column= oos.readInt();
                    guesses= (int [][]) oos.readObject();
                    mines=(int[][]) oos.readObject();
                    mostrartiempo=oos.readInt();
                   
                    
                    b = new JButton[n][m];
                     //Se lee los String de los valores de los botones uno a uno y se crea de nuevo la matriz
                    for(int i=0;i<n;i++){
                        for(int j=0;j<m;j++){
                            b[i][j] =(new JButton((String) oos.readObject()));
                           
                        }
                    }
                    frame.dispose();
                    new Buscaminas(n,m,nomines,nivel,newmines,perm,tmp,found,row,column,guesses,b,mines,mostrartiempo);
                    
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
     //Opcion nuevo juego funciona igual que en el constructor 1
     nuevoJuego.addActionListener(new ActionListener() {
 	        public void actionPerformed(ActionEvent ev) {
 	            frame.dispose();
 	            Opciones();
 	    }
 	   });
     //Opcion guardar funcion igual que en el constructor 1
     guardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {

                ObjectOutputStream oos = null;
                try {
                   oos = new ObjectOutputStream(new FileOutputStream("partida.txt"));
                    
                   oos.writeInt(n);
                    oos.writeInt(m);
                   oos.writeInt(nomines);
                    oos.writeObject(nivel);
                    oos.writeInt(newmines);
                    oos.writeObject(perm);
                    oos.writeObject(tmp);
                    oos.writeBoolean(found);
                   oos.writeInt(row);
                    oos.writeInt(column);
                    oos.writeObject(guesses);
                  
                    oos.writeObject(mines);
                    oos.writeInt(mostrartiempo);
                    //se pasan los valores de los JButton uno a uno al fichero ( daba excepcion si se pasa la matriz original)
                    for (JButton[] boton : b){
                        for (JButton boton2 : boton){
                            oos.writeObject(boton2.getText());
                            
                           
                           
                        }
                    }
                   
               
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        oos.close();
                    } catch (IOException ex) {
                        Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
             
        });
     //Creacion del menú
      menumb.add(menu1);
      menu1.add(reiniciar);
      menu1.add(nuevoJuego);
      frame.setJMenuBar(menumb);
      menumb.add(minas);
      menumb.add(tiempo);
      menu1.add(guardar);
      menu1.add(cargar);
     //Creación del contenido del frame, es distinto del constructor 1 ya que se carga una partida a medias
      frame.setLayout(new GridLayout(n,m));
        
       
        
        for (int y = 0;y<m;y++){//Para cada elemento de b(array de botones que es la pantalla de minas)
            for (int x = 0;x<n;x++){
                
                b[x][y].addActionListener(this);
                b[x][y].addMouseListener(this);
                if(b[x][y].getText().equals("x"))//Si el boton era una X, se marca la casilla con naranja 
                     b[x][y].setBackground(Color.orange);
                //Si la casilla era un numero era una casilla que el usuario habia desbloqueado y se marcan como false todas.
                if(b[x][y].getText().equals("1"))
                     b[x][y].setEnabled(false);
                if(b[x][y].getText().equals("2"))
                     b[x][y].setEnabled(false);
                 if(b[x][y].getText().equals("3"))
                     b[x][y].setEnabled(false);
                if(b[x][y].getText().equals("4"))
                     b[x][y].setEnabled(false);
                if(b[x][y].getText().equals("5"))
                     b[x][y].setEnabled(false);
                if(b[x][y].getText().equals("6"))
                     b[x][y].setEnabled(false);
                if(b[x][y].getText().equals("7"))
                     b[x][y].setEnabled(false);
                if(b[x][y].getText().equals("8"))
                     b[x][y].setEnabled(false);
               
               frame.add(b[x][y]); 
                if(b[x][y].getText().equals(" "))//si el elemento era casilla vacia tambien se marca a falso.
                    b[x][y].setEnabled(false);
                
                
            }//end inner for
        }//end for
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        
        for (int y = 0;y<m+2;y++){
            for (int x = 0;x<n+2;x++){
                System.out.print(mines[x][y]);
            }
           
        System.out.println("");}
        
    }//end constructor Mine()
        public static void Opciones() {//Función que crea el MenúPrincipal y da opcion a elegir un nivel de dificultad y a unas opciones de menú(ver tiempos,
 	   //creacion del frame
 	   JFrame principal = new JFrame("BUSCAMINAS");
           principal.setLocationRelativeTo(null);
 	   principal.setDefaultCloseOperation(EXIT_ON_CLOSE);
           principal.setSize(350, 250);
 	  //creacion del menu
 	   JMenuBar menumb = new JMenuBar();
 	   JMenu menu1= new JMenu("Opciones");
 	  
 	   JMenuItem nuevoJuego = new JMenuItem("Nuevo Juego");
           JMenu record = new JMenu("Records");
           JMenuItem recordp= new JMenuItem("Tiempos Principiante");
           JMenuItem recordi= new JMenuItem("Tiempos Intermedio");
           JMenuItem recorde= new JMenuItem("Tiempos Experto");
           JMenuItem cargarp= new JMenuItem("CargarPartida");
           menu1.add(cargarp);
           menu1.add(record);
           record.add(recordp); 
           record.add(recordi);
           record.add(recorde);
 	   menumb.add(menu1);
           //Opcion de cargar igual que en el constructor
 	   cargarp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                   int n,m,nomines,newmines,row,column,mostrartiempo;
                double starttime;
                int[][] perm,guesses,mines;
                String nivel,tmp;
                JButton [][] b;
                boolean found;
                   ObjectInputStream oos = null;
                try {
                   oos = new ObjectInputStream(new FileInputStream("partida.txt"));
                    
                    
                    n= oos.readInt();
                    m=oos.readInt();
                    nomines=oos.readInt();
                    nivel = (String)oos.readObject();
                    newmines=oos.readInt();
                    perm=(int[][]) oos.readObject();
                    tmp= (String) oos.readObject();
                    found= oos.readBoolean();
                    row= oos.readInt();
                    column= oos.readInt();
                    guesses= (int [][]) oos.readObject();
                    mines=(int[][]) oos.readObject();
                    mostrartiempo=oos.readInt();
                    
                    
                    b = new JButton[n][m];

                    for(int i=0;i<n;i++){
                        for(int j=0;j<m;j++){
                            b[i][j] =(new JButton((String) oos.readObject()));
                           
                        }
                    }
                    principal.dispose();
                    new Buscaminas(n,m,nomines,nivel,newmines,perm,tmp,found,row,column,guesses,b,mines,mostrartiempo);
                    
                   
                    
                    } catch (FileNotFoundException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
           
                
            });
 	   principal.setJMenuBar(menumb);
 	   menu1.add(nuevoJuego);
           //Opcion de juevo nuevo vuelve a cargar la misma pantalla ya que estamos en la de Opciones que es la principal
 	   nuevoJuego.addActionListener(new ActionListener() {
 	        public void actionPerformed(ActionEvent ev) {
 	            principal.dispose();
 	            Opciones();
 	    }
 	   });
           //Muestra los tiempos de la dificultad principiante guardados en un fichero
 	   recordp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    // Se elimina el frame actual
                    principal.dispose();
                    //Se crea el frame que va a mostrar los tiempos 
                    JFrame f= new JFrame("TIEMPOS PRINCIPIANTE");
                    f.setSize(450, 250);
                    int count=1;
                    f.setLocationRelativeTo(null);
                    f.setDefaultCloseOperation(EXIT_ON_CLOSE);
                    f.setLayout(new GridLayout(11,1));
                    //Carga el fichero de niveles principiante
                    File file= new File("principiante.txt");
                    file.createNewFile();
                    String textLine;
                    //se lee y se cogen todos los tiempos y se copian enel frame en JLabel
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsoluteFile()), "ISO-8859-1"));
                    while ((textLine = reader.readLine())!= null){
                        JLabel linea= new JLabel(count+"-"+textLine);
                        
                        f.add(linea);
                        count++;
                    }
                    //se añade un boton de volver que vuelve al inicio, al menú principal
                    JButton volver = new JButton("Volver Inicio");
                    f.add(volver);
                    volver.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            f.dispose();
                            Opciones();
                        }
                    });
                    f.setVisible(true);
                    
                } catch (IOException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                }
               
            }
        });
           //Se hace exactamente lo mismo que para los tiempos de principiante pero con el fichero que guarda los tiempos intermedios
           recordi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    principal.dispose();
                    JFrame f= new JFrame("TIEMPOS INTERMEDIO");
                    f.setSize(450, 250);
                    int count=1;
                     f.setLocationRelativeTo(null);
                    f.setDefaultCloseOperation(EXIT_ON_CLOSE);
                    f.setLayout(new GridLayout(11,1));
                    //apertura del fichero de tiempos intermedios.
                    File file= new File("intermedio.txt");
                    file.createNewFile();
                    String textLine;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsoluteFile()), "ISO-8859-1"));
                    while ((textLine = reader.readLine())!= null){
                        JLabel linea= new JLabel(count+"-"+textLine);
                        
                        f.add(linea);
                        count++;
                    }
                    JButton volver = new JButton("Volver Inicio");
                    f.add(volver);
                    volver.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            f.dispose();
                            Opciones();
                        }
                    });
                    f.setVisible(true);
                   
                } catch (IOException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                }
               
            }
        });
           //Igual que en principiante e intermedio pero con los tiempos de experto.
           recorde.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                  try {
                     principal.dispose();
                     JFrame f= new JFrame("TIEMPOS EXPERTO");
                     f.setSize(450, 250);
                    int count=1;
                     f.setLocationRelativeTo(null);
                    f.setDefaultCloseOperation(EXIT_ON_CLOSE);
                    f.setLayout(new GridLayout(11,1));
                    //apertura de fichero de experto
                    File file= new File("experto.txt");
                    file.createNewFile();
                    String textLine;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsoluteFile()), "ISO-8859-1"));
                    while ((textLine = reader.readLine())!= null){
                        JLabel linea= new JLabel(count+"-"+textLine);
                        
                        f.add(linea);
                        count++;
                    }
                    JButton volver = new JButton("Volver Inicio");
                    f.add(volver);
                    volver.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            f.dispose();
                            Opciones();
                        }
                    });
                    f.setVisible(true);
                   
                } catch (IOException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                }
               
            
                  }
                
            });
           //botones de las opciones de nivel de dificultad
 	   JButton principiante = new JButton("Principiante");
 	   JButton  intermedio = new JButton("Intermedio");
 	   JButton experto = new JButton("Experto");  
 	   JButton   personalizado = new JButton("Personalizado");
 	   // si se pulsa principiante se crea un nuevo Buscaminas con los atributos del nivel principiante
 	   principiante.addActionListener(new ActionListener() {

 			public void actionPerformed(ActionEvent e) {
 				intermedio.setSelected(false);
 				experto.setSelected(false);
 				personalizado.setSelected(false);
 				int n = 10;
 				int m = 10;
 				int	nomines = 10;
 				principal.dispose();
                               
                                
                             Buscaminas buscaminas = new Buscaminas(n,m,nomines,"principiante");//Buscaminas que crea un nivel principiante
                             
 			}
 	   });
           // si se pulsa intermedio se crea un nuevo Buscaminas con los atributos del nivel intermedio
 	   intermedio.addActionListener(new ActionListener() {

 			public void actionPerformed(ActionEvent e) {
 				experto.setSelected(false);
 				principiante.setSelected(false);
 				personalizado.setSelected(false);
 				int	n = 16;
 				int	m = 16;
 				int	nomines = 40;
 				principal.dispose();
 				Buscaminas buscaminas = new Buscaminas(n,m,nomines,"intermedio");//Buscaminas que crea un nivel intermedio
                            
 			}
 	   });
           //// si se pulsa experto se crea un nuevo Buscaminas con los atributos del nivel experto
 	   experto.addActionListener(new ActionListener() {

 			public void actionPerformed(ActionEvent e) {
 				intermedio.setSelected(false);
 				principiante.setSelected(false);
 				personalizado.setSelected(false);
 				int	n = 23;
 				int	m = 23;
 				int	nomines = 99;
 				principal.dispose();
 				Buscaminas buscaminas = new Buscaminas(n,m,nomines,"experto");//Buscaminas que crea un nivel experto
                            
 			}
 	   });
           // si se pulsa Personalizado se crea un nuevo Buscaminas con los atributos del nivel personalizado
 	   personalizado.addActionListener(new ActionListener() {

 			public void actionPerformed(ActionEvent e) {
 				intermedio.setSelected(false);
 				principiante.setSelected(false);
 				experto.setSelected(false);
                                principal.dispose();
                                 
 				JFrame marco = new JFrame("Personalizado");
                                
                                marco.setDefaultCloseOperation(EXIT_ON_CLOSE);
                                 marco.setSize(450, 250);
 				marco.setLayout(new GridLayout(8,4));
                                 marco.setLocationRelativeTo(null);
 				
 				
 				   //al pulsar personalizado primero se crea otro Frame en el que se permiten ingresar las medidas y las minas
 				JLabel titulo = new JLabel("Inserta las medidas(Solo se permiten filas y columnas iguales): ");
 				marco.add(titulo);
 				
 				
 				JLabel etN = new JLabel("columnas/filas:");
 				etN.setSize(10,10);
 				marco.add(etN);
 				JTextField inN = new JTextField();
 				inN.setBounds(50,135,50,25);
 				marco.add(inN);
 				
 				JLabel etMi = new JLabel("minas:");
 				etMi.setSize(10,10);
 				marco.add(etMi);
 				JTextField inMin = new JTextField();
 				inMin.setBounds(200,135,50,25);
 				marco.add(inMin);
 				
 				JButton boton = new JButton("Aceptar");
 				//boton.setSize(500, 50);

 				marco.add(boton);
                                
                                marco.setVisible(true);
                                //marco.pack();
 				boton.addActionListener(new ActionListener() {

 		 			public void actionPerformed(ActionEvent e) {
 		 				int	n = Integer.parseInt(inN.getText());
 		 				int	m = Integer.parseInt(inN.getText());
 		 				int	nomines = Integer.parseInt(inMin.getText());
 		 				marco.dispose();
 		 				
 		 				new Buscaminas(m,n,nomines,"personalizado");//Al darle a aceptar se crea un nuevo Buscaminas con los parámetros introducidos.
 		 				
 		 			}
 		 	   });
 				
 			}
 			});
 	   
    
 	  
 	   principal.setLayout(new GridLayout(4,1));
 	   principal.add(principiante);
 	   principal.add(intermedio);
 	   principal.add(experto);
 	   principal.add(personalizado);
 	   
 	   

        //principal.pack();
        principal.setVisible(true);
    }
        //Codigo original de la funcion actionPerformed
    public void actionPerformed(ActionEvent e){
        found =  false;
        JButton current = (JButton)e.getSource();
        for (int y = 0;y<m;y++){
            for (int x = 0;x<n;x++){
                JButton t = b[x][y];
                if(t == current){
                    row=x;column=y; found =true;
                }
            }//end inner for
        }//end for
        if(!found) {
            System.out.println("didn't find the button, there was an error "); System.exit(-1);
        }
        Component temporaryLostComponent = null;
        if (b[row][column].getBackground() == Color.orange){
            
            return;
        }else if (mines[row+1][column+1] == 1){
                timer.cancel();
                JOptionPane.showMessageDialog(temporaryLostComponent, "You set off a Mine!!!!.");
                System.exit(0);
        } else {
            tmp = Integer.toString(perm[row][column]);
            if (perm[row][column] == 0){
                    tmp = " ";
            }
            b[row][column].setText(tmp);
            b[row][column].setEnabled(false);
            try {
                checkifend();
            } catch (IOException ex) {
                Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (perm[row][column] == 0){
                scan(row, column);
                try {
                    checkifend();
                } catch (IOException ex) {
                    Logger.getLogger(Buscaminas.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
 //Codigo de la funcion checkifend original con alguna modificación
    public void checkifend() throws IOException{
        int check= 0;
        for (int y = 0; y<m;y++){
            for (int x = 0;x<n;x++){
        if (b[x][y].isEnabled()){
            check++;
        }
            }}
        if (check == nomines){
            timer.cancel();
           
            Component temporaryLostComponent = null;
            //Al acabar, si el usuario no esta en personalizado se comprueba si ha superado un record, si lo supera se le da la opción de poder guardar el tiempo con un nombre o no.
            if(!nivel.equals("personalizado")){
            if (checkFichero()){
                
              String name =JOptionPane.showInputDialog(temporaryLostComponent, "Congratulations you won!!! It took you "+mostrartiempo+" seconds!\n You got a new record in this level. Insert a player name if you want to save your time, press cancel if you dont want to save it.\n");
              if(name!=null){
                while (name.contains(" ")){
                    name=JOptionPane.showInputDialog(temporaryLostComponent, "Congratulations you won!!! It took you "+mostrartiempo+" seconds!\n You got a new record in this level. Insert a player name if you want to save your time, press cancel if you dont want to save it.\n(Whitespace are forbbiden)");
                    if(name==null)
                        break;
                }
                if(name!=null)
                addPlayerTime(name);
              }
            }
            else{
                JOptionPane.showMessageDialog(temporaryLostComponent, "Congratulations you won!!! It took you "+mostrartiempo+" seconds!");
            }
            }else{
                 JOptionPane.showMessageDialog(temporaryLostComponent, "Congratulations you won!!! It took you "+mostrartiempo+" seconds!");
            }
        }
    }
 //Funcion scan original
    public void scan(int x, int y){
        for (int a = 0;a<8;a++){
            if (mines[x+1+deltax[a]][y+1+deltay[a]] == 3){
 
            } else if ((perm[x+deltax[a]][y+deltay[a]] == 0) && (mines[x+1+deltax[a]][y+1+deltay[a]] == 0) && (guesses[x+deltax[a]+1][y+deltay[a]+1] == 0)){
                if (b[x+deltax[a]][y+deltay[a]].isEnabled()){
                    b[x+deltax[a]][y+deltay[a]].setText(" ");
                    b[x+deltax[a]][y+deltay[a]].setEnabled(false);
                    scan(x+deltax[a], y+deltay[a]);
                }
            } else if ((perm[x+deltax[a]][y+deltay[a]] != 0) && (mines[x+1+deltax[a]][y+1+deltay[a]] == 0)  && (guesses[x+deltax[a]+1][y+deltay[a]+1] == 0)){
                tmp = new Integer(perm[x+deltax[a]][y+deltay[a]]).toString();
                b[x+deltax[a]][y+deltay[a]].setText(Integer.toString(perm[x+deltax[a]][y+deltay[a]]));
                b[x+deltax[a]][y+deltay[a]].setEnabled(false);
            }
        }
    }
 //Funcion perimcheck original
    public int perimcheck(int a, int y){
        int minecount = 0;
        for (int x = 0;x<8;x++){
            if (mines[a+deltax[x]+1][y+deltay[x]+1] == 1){
                minecount++;
            }
        }
        return minecount;
    }
 
    public void windowIconified(WindowEvent e){
 
    }
 
    public static void main(String[] args){
        //new Buscaminas();
        Opciones();
    }
 
    public void mouseClicked(MouseEvent e) {
 
    }
 
    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }
 
    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }
 //Funcion mousePressed original con algunas modificaciones 
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            found =  false;
            Object current = e.getSource();
            for (int y = 0;y<m;y++){
                    for (int x = 0;x<n;x++){
                            JButton t = b[x][y];
                            if(t == current){
                                    row=x;column=y; found =true;
                            }
                    }//end inner for
            }//end for
            if(!found) {
                System.out.println("didn't find the button, there was an error "); System.exit(-1);
            }
            if ((guesses[row+1][column+1] == 0) && (b[row][column].isEnabled())){
                b[row][column].setText("x");
                guesses[row+1][column+1] = 1;
                b[row][column].setBackground(Color.orange);
                //Al marcar una mina se actualiza la etiqueta que muestra las minas , quitando una.
                newmines--;
                minas.setText("Minas:"+newmines+" ");
                
            } else if (guesses[row+1][column+1] == 1){
                b[row][column].setText("?");
                guesses[row+1][column+1] = 0;
                b[row][column].setBackground(null);
                //Al quitar la marca de una mina, se actualiza la etiqueta que muestra el número restando sumando una.
                newmines++;
                minas.setText("Minas:"+newmines+" ");
            }
        }
    }
 
    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }
    //Funcion checkFichero que comprueba que el tiempo que se acaba de lograr es record o no.
    private boolean checkFichero() throws IOException {
         File f;
        
        ArrayList<String> tiempos = new ArrayList<>();
        String textLine;
        //Según la varable nivel se sabe que nivel se está jugando
        if(nivel.equals("principiante")){
        f= new File("principiante.txt");
        f.createNewFile();
        }else if(nivel.equals("intermedio")){
            f= new File("intermedio.txt");
            f.createNewFile();
        }else{
            f= new File("experto.txt");
            f.createNewFile();
        }
        //lectura de todos los tiempos
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f.getAbsoluteFile()), "ISO-8859-1"));
        while ((textLine = reader.readLine())!= null){
        tiempos.add(textLine);
        }
        // se realizan las comprobaciones de tiempo según todos los casos posibles
        if(tiempos.size()<10){//Si hay menos de 10 elementos se devuelve true
            return true;
        }else {
            String tiempo = tiempos.get(9);
            String[] split = tiempo.split(" ");
            int n = Integer.parseInt(split[1]);
            if(mostrartiempo<n)//Si el tiempo es menor del que esta metido, se devuelve true
                return true;
        }
        return false;//Si se llega aqui se devuelve false
    }

    private void addPlayerTime(String name) throws IOException { // En caso de que se supere un record y el usuario inserte un nombre se añade
        
        ArrayList<String> tiempos = new ArrayList<>();
        String textLine;
        PrintWriter writer;
       File f;
       //se abre el fichero correspondiente
         if(nivel.equals("principiante")){
             f= new File("principiante.txt");
            
        }else if(nivel.equals("intermedio")){
            f= new File("intermedio.txt");
            
        }else{
            f= new File("experto.txt");
            
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f.getAbsoluteFile()), "ISO-8859-1"));
        while ((textLine = reader.readLine())!= null){
          
        tiempos.add(textLine);//se cogen los tiempos
        }
        //se comprueban uno a uno hasta averiguar la posicion en la que va el nuevo tiempo
        for (int i=0; i<=tiempos.size();i++){
            if(i==10)
                break;
            if(i==tiempos.size()){
                tiempos.add(name+" "+mostrartiempo+" segundos"); //Si no hay 10 elementos y se llega al final se añade al final .
                break;
            }
            //Comprobacion uno a uno del tiempo que estaba insertado.
             String tiempo = tiempos.get(i);
             String[] split = tiempo.split(" ");
             int n = Integer.parseInt(split[1]); //
                if(mostrartiempo<n){ //Cuando se encuentra la posicion donde va, se inserta
                    tiempos.add(i,name+" "+mostrartiempo+" segundos");
                    if(tiempos.size()==11)
                        tiempos.remove(10);// y se borra el elemento que se queda como ultimo
                    break;
                 }
        }
        //Se vuelven a escribir los tiempos por orden y solo los 10 resultantes o los que haya si hay menos
         if(nivel.equals("principiante")){
            writer =new PrintWriter("principiante.txt", "UTF-8");
            
        }else if(nivel.equals("intermedio")){
             writer =new PrintWriter("intermedio.txt", "UTF-8");
            
        }else{
            writer =new PrintWriter("experto.txt", "UTF-8");
            
        }
         
        for(String s:tiempos){
        writer.println(s);//se escriben línea a linea.
       
        }
        writer.close();
    }
}//end class
