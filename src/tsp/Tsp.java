package tsp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;
import java.awt.Dimension;
import javax.swing.JFrame;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.collections15.Transformer;


public class Tsp {
    final static String filename = "tc3";
    final static String inname = filename + ".in"; 
    final static int max = 1000;
    
    public static double[] twosmallest(double[] elements){
        double smallest = Double.MAX_VALUE;
        double secondSmallest = Double.MAX_VALUE;
        for (int i = 0; i < elements.length; i++) {
            if(elements[i]==smallest){
              secondSmallest=smallest;
            } else if (elements[i] < smallest) {
                secondSmallest = smallest;
                smallest = elements[i];
            } else if (elements[i] < secondSmallest) {
                secondSmallest = elements[i];
            }
        }
        double[] ret = {smallest,secondSmallest};
        return ret;
    } 
    
    public static Tree<Double> akar(double[][] m){
        double sum = 0;
        for (int i=0;i<m.length;i++){
            double[] ss = twosmallest(m[i]);
            sum += ss[0] + ss[1];
        }
        return new Tree<Double>(sum/(double)2,0);
    }
    
    public static boolean[] tambahcek(boolean[] b, Tree t){
        while (t!= null){
            b[t.id] = true;
            t = t.parent;
        }
        return b;
    }
    
    public static double[] removeZero(double[] array){
        int j = 0;
        for( int i=0;  i<array.length;  i++ )
        {
            if (array[i] != 0)
                array[j++] = array[i];
        }
        double[] newArray = new double[j];
        System.arraycopy( array, 0, newArray, 0, j );
        return newArray;
    }
    /*
    public Tree searchMin(Tree t) {
        double min = 9999.0;
        if (t.children.isEmpty()) {
            if ((double)t.data < min){
                min = (double) t.data;
                return t;
            }
        } else {
            for (int i=0; i<t.children.size();i++){
                min = searchMin(t.children)
            } 
        }
        return t;
    }*/
    
    public static void cetakSolusi(Tree t, double bobot){
        //membentuk rute tur dari tree
        String tur = "1";
        while (t!= null){
            tur = (t.id+1) + tur;
            t = t.parent;
        }
        System.out.println("Tur terpendek adalah " + tur + " dengan bobot " + bobot);
    }
    
    public static String[] bobotTurLengkap(double[][] m){
        long startTime = System.nanoTime(); 
        int num = 0;
        double bobot = Double.MAX_VALUE;
        String[] retval = new String[m.length*2+2];
        //int[] cost = new int[m.length-1];
        //int cursor = 1;
        Tree<Double> t = akar(m);
        num += 1;
        //System.out.println(t.id);
        //for (int j=0;j<m.length-1;j++){
        //while (t.children != null){
        while (t.getLevel() <= m.length-1){
            double minimum = 9999.0;
            int idxmin = -1;
            boolean[] ceklist = new boolean[105];
            for (int i=t.getLevel()+1;i<m.length;i++){
                //menambah list yang harus dimasukkan
                Vector<Integer> vi = new Vector<>();
                Vector<Integer> vj = new Vector<>();
                
                //System.out.println(t.nai.toString()+" "+t.naj.toString());
                for (int z = 0; z < t.nai.size();z++){
                    vi.add(t.nai.get(z));
                }
                for (int z = 0; z < t.naj.size();z++){
                    vj.add(t.naj.get(z));
                }
                
                //System.out.println(vi.toString()+" "+vj.toString());
                vi.add(t.id);
                
                int ret = 1;
                ceklist = tambahcek(ceklist,t);
                
                while (ceklist[ret] == true)
                    ret++;
               
                vj.add(ret);
                ceklist[ret] = true;
                
                //System.out.println(vi.toString()+" "+vj.toString());
                
                //agar m bisa diubah nilainya
                double[][] mtemp = new double[m.length][m.length];
                for (int ii= 0; ii< m.length;ii++)
                    for (int jj=0; jj<m.length;jj++)
                        mtemp[ii][jj] = m[ii][jj]; 


                //swap semua occurance di list yang harus dimasukkan
                for (int k=0;k<vi.size();k++){
                    mtemp[vi.get(k)][vj.get(k)] /= max;
                    mtemp[vj.get(k)][vi.get(k)] /= max;
                }
                //printMatrix(mtemp);

                //hitung nilai bobot
                double sum = 0;
                for (int k=0;k<mtemp.length;k++){
                    double[] ss = twosmallest(mtemp[k]);
                    if (ss[0]<1)
                        ss[0] *= max;
                    if (ss[1]<1)
                        ss[1] *= max;
                    sum += ss[0] + ss[1];
                    //System.out.println(ss[0]+" "+ss[1]);
                }

                //tambahkan nilai sebagai anak
                t.addChild((double)sum/(double)2, ret);
                num++;
                //System.out.println(i);
                for (int k=0;k<vi.size();k++)
                    t.children.get(i-t.getLevel()-1).nai.add(vi.get(k));
                for (int k=0;k<vj.size();k++)
                    t.children.get(i-t.getLevel()-1).naj.add(vj.get(k));

                //pilih yang bobot paling minimum
                if (minimum > t.children.get(i-t.getLevel()-1).data){
                    minimum = t.children.get(i-t.getLevel()-1).data;
                    idxmin = i-t.getLevel()-1;
                }
                
                vi.clear();
                vj.clear();
                
            }
            //System.out.println(t.toString());
            //System.out.println(t.children.toString());
            
            //bobot = t.children.get(0).data;
            while (t.parent != null){
                t = t.parent;
            }
            Vector<Tree<Double>> sets = t.getAllLeafNodes();
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            Collections.sort(sets, new Comparator<Tree>() {
                @Override
                public int compare(Tree o1, Tree o2) {
                    return (int) ((double)o1.data - (double)o2.data); // Ascending//To change body of generated methods, choose Tools | Templates.
                }
            });
            
            if (t.children.get(0).getLevel()==m.length-1) {
                for (int k=0;k<sets.size();k++){
                    if (t.children.get(0).data < sets.get(k).data){
                        sets.get(k).mati = true;
                        sets.remove(k);
                    }
                }
            }
            //System.out.println(sets.toString());
            t = sets.firstElement();
            
            if (t.getLevel()==m.length-1) {
                t.mati = true;
                bobot = sets.get(0).data;
                cetakSolusi(t,bobot);
                for (int k=0;k<t.nai.size();k++){
                    retval[k] = ""+(t.nai.get(k)+1)+(t.naj.get(k)+1);
                    retval[k+t.nai.size()] = ""+(t.naj.get(k)+1)+(t.nai.get(k)+1);
                    //System.out.println(retval[k] + " " + retval[k+t.nai.size()]);
                }
                //retval[t.nai.size()*2] = "16";
                //retval[t.nai.size()*2+1] = "61";
            }
            //break;
            
            //if (t.children!=null)
            //    t = t.children.get(idxmin);
            System.out.println(t.children);
            System.out.println(t.nai.toString()+" "+t.naj.toString());
        }
        //System.out.println(minimum);        
        //System.out.println(idxmin);
        
        //num += (m.length-1)*(m.length-1);
        
        Vector<Integer> retx = t.nai;
        Vector<Integer> rety = t.naj;
        
        
        
        //menghitung waktu
        long estimatedTime = System.nanoTime() - startTime;
        double elapsedSeconds = estimatedTime / 1000000.0;
        
        System.out.println("Waktu eksekusi : " + elapsedSeconds + " milidetik");
        System.out.println("Jumlah simpul yang dibangkitkan : " + num);
        return retval;
    }
    
    public static void main(String[] args) {
        double[][] matrix = getInput();
        final String[] s = bobotTurLengkap(matrix);
        //Tree<Double> t = akar(matrix);
        //System.out.println(t.toString());
        //printMatrix(matrix);
        
        int vert = matrix.length;
        Graph<Integer, String> g = new SparseGraph<Integer, String>();
        for(int i = 0; i < vert; i++) 
            g.addVertex(i+1);
        for (int i=0;i<vert-1;i++){
            for (int j=i+1;j<vert;j++){
                g.addEdge(""+(i+1)+(j+1), i+1, j+1);
            }
        }
        
        Layout<Integer, String> layout = new CircleLayout<Integer, String>(g);
        layout.setSize(new Dimension(300,300));

        // VisualizationServer actually displays the graph
        BasicVisualizationServer<Integer,String> vv = new BasicVisualizationServer<Integer,String>(layout);
        vv.setPreferredSize(new Dimension(350,350)); //Sets the viewing area size
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        // Transformer maps the vertex number to a vertex property
        Transformer<String,Paint> edgeColor = new Transformer<String,Paint>() {
            public Paint transform(String i) {
                for (String item : s) {
                    if (i.equals(item)) {
                        return Color.GREEN;
                    }
                }
                return null;
            }
        };
        vv.getRenderContext().setEdgeFillPaintTransformer(edgeColor);
        //vv.getRenderContext().setVertexShapeTransformer(vertexSize);

        JFrame frame = new JFrame();
        frame.getContentPane().add(vv);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static double[][] getInput() {
        try{
            Scanner in = new Scanner(new BufferedReader(new FileReader(inname)));
            int t = in.nextInt();
            double[][] matrix = new double[t][t];
            in.nextLine();
            for (int i = 0; i < t; i++){
                for (int j = 0; j < t; j++){
                    matrix[i][j] = in.nextDouble();
                }
                in.nextLine();
            }
            in.close();
            return matrix;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void printMatrix(double[][] m){
        for (int i = 0; i < m.length; i++){
            for (int j = 0; j < m.length; j++){
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
    }
}
