
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.experiments.Experiment_HH_2obj;
import jmetal.util.JMException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vinicius
 */
public class main {
    public static void main(String[] args){
        try {
            //Experiment_SLMABHH_2obj.main(args);
            Experiment_HH_2obj.main(args);
            //Combined_MOEAD_2obj.main(args);
            //Combined_NSGAII_2obj.main(args);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
