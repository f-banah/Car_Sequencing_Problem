package chocopov;

import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import static choco.Choco.*;

public class CSVPOV {
	
	public CSVPOV() {
		this.init();
		this.NbreVConstraint();
		this.OptionConstraint();
		this.SeqConstraint();
		this.solve();
	} 
	
	int v = 25; // Nombre de voitures à produire
	int o = 5; // Nombre des options disponibles
	int c = 12; // Nombre de catégories
	
	// Listes d'entier pour chaque option i; p[i]/q[i] = la fréquence d'apparition
	int[] p = {1,2,1,2,1};
	int[] q = {2,3,3,5,5}; 
	
	int[] d = {3,1,2,4,3,3,2,1,1,2,2,1}; // demande
	int[][] r = {
			{0,1,0,0,0},
            {1,0,1,0,1},
            {1,1,0,0,0},
            {0,1,0,1,0},
            {0,1,0,0,1},
            {0,0,0,1,0},
            {1,1,1,0,0},
            {1,0,0,1,0},
            {1,0,1,0,0},
            {0,0,1,0,0},
            {0,1,1,0,0},
            {1,1,0,1,0}
    }; //  Matrice de booléen ri,j = 1, représente le fait que l’option Oj est présente sur le véhicule Vi, 0 sinon.
	
	Model model = new CPModel();
	Solver solver = new CPSolver();
	
	private IntegerVariable[]  categoV;
	private IntegerVariable[][] optionV;
	
	public void init() {
		this.categoV = makeIntVarArray("classement Voiture", this.v, 1, this.c);
		this .optionV =  makeIntVarArray("optionV", this.o, this.v, 0, 1);
	}
	
	public void NbreVConstraint() {
		for(int i = 0; i < c; i++) {
			this.model.addConstraint(Choco.occurrence(d[i], this.categoV, i+1));
		}
	}
	
	public void OptionConstraint() {
		for (int i = 0; i < this.c; i++) {
        	
            for (int j = 0; j < this.v; j++) {
                Constraint[] C = new Constraint[this.o];  
                for (int k = 0; k < this.o; k++) {
                	C[k] = eq(this.optionV[k][j], r[i][k]);
                }
                    
                model.addConstraint(implies(eq(this.categoV[j], i+1),and(C)));
            }
        }
	}

	public void SeqConstraint() {
		for(int i = 0; i < this.o; i++) {
            for(int j = 0; j < this.v-q[i]+1; j++) {
                IntegerExpressionVariable somme = ZERO ;
                for(int c = 0; c < q[i]; c++) {
                    somme = Choco.plus(somme,this.optionV[i][j+c]);
                }
                model.addConstraint(Choco.leq(somme,p[i]));
            }
        }
	}
	
	public void solve() {
		this.solver.read(model);
		
		this.solver.solve();
		
		
			
            System.out.println("Résolution CSP avec Choco-Solver : ");

            System.out.println("Class  \t    Required options");
            for (int i = 0; i < this.v; i++) {
                System.out.print("  "+(solver.getVar(this.categoV[i]).getVal() - 1)+"\t   \t");
                for (int j = 0; j < this.o; j++) {
                    System.out.print(solver.getVar(this.optionV[j][i]).getVal() +" ");
                }
                System.out.println("");
            }
            System.out.println("");
            

}

	public static void main(String[] args) {

		new CSVPOV();
}
}
	
