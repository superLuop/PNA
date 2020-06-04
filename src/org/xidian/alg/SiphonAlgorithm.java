package org.xidian.alg;

import java.util.Vector;

import org.xidian.model.Marking;
import org.xidian.model.Matrix;
import org.xidian.model.PetriModel;
import org.xidian.model.Transition;
import org.xidian.temp.PNMatrix;

/**
 * 信标分析
 *
 * @author luopeng
 * <p>
 * 未重构代码
 */
public class SiphonAlgorithm extends PetriModel {

    public SiphonAlgorithm() {

    }

    public SiphonAlgorithm(Matrix preMatrix, Matrix posMatrix, Transition transition, Marking ininmarking) {
        super(preMatrix, posMatrix, transition, ininmarking);
    }

    /**
     *
     */
    public String analyse() {
        String output = "Minimal Siphons\n";
        // compute siphons
        Vector<boolean[]> siphons = findAllMinimalSiphons(
                new PetriNet(new PNMatrix(preMatrix.getMatrix()),
                        new PNMatrix(posMatrix.getMatrix())),
                new SetOfPlaces(placesCount));
        output += toString(siphons);

        output += "\nMinimal Traps\n";
        // now, compute traps switching forwards and backwards incidence matrices
        Vector<boolean[]> traps = findAllMinimalSiphons(
                new PetriNet(new PNMatrix(posMatrix.getMatrix()),
                        new PNMatrix(preMatrix.getMatrix())),
                new SetOfPlaces(placesCount));
        output += toString(traps);
        return output;
    }


    private String toString(Vector<boolean[]> vector) {
        String s = "";

        if (vector.size() == 0) {
            return "Not found";
        }

        for (boolean[] element : vector) {
            s += "{";
            for (int i = 0; i < element.length; i++) {
                s += element[i] ? (i + 1) + ", "
                        : "";
            }
            // replace the last occurance of ", "
            s = s.substring(0, (s.length() - 2)) + "}\n";
        }
        return s;
    }


    /**
     * findAllMinimalSiphons()
     * Finds all minimal siphons in a given Petri Net that contain a specific
     * set of places.
     * @param G  A Petri Net
     * @param Ptilde A set of places that each siphon must contain
     * return  A vector containg all minimal siphons found
     */
    private Vector<boolean[]> findAllMinimalSiphons(PetriNet G, SetOfPlaces Ptilde) {
        Vector<boolean[]> E; // contains all minimal siphons found
        SetOfPlaces S = new SetOfPlaces(Ptilde.size()); // a siphon

        // Step 1
        E = new Vector<boolean[]>();

        // Step 2
        // check if there is a place with an empty pre-set
        int p = G.placeWithEmptyInputSet();
        while (Ptilde.isEmpty() && (p != -1)) {

            // Step 3
            S.add(p);
            E.add(S.getPlaces());
            // the Petri Net can be reduced by eliminating p
            G = reduceNet(G, Ptilde.getPlacesMinus(p));
            // check if there is another place with an empty pre-set
            p = G.placeWithEmptyInputSet();
        }

        // Step 4
        // Find a generic siphon
        SetOfPlaces Stilde = findSiphon(G, Ptilde);

        // Step 5
        if (Stilde.isEmpty()) {
            // No siphon has been found, return E
            return E;
        }

        // Step 6
        // Find a minimal siphon contained in the generic siphon
        S = findMinimalSiphon(G, Stilde, Ptilde);

        // problem!
        // here should be simply E.add(S.getPlaces());
        // but without this extra check, non minimal siphons were computed.
        // So, a we find a new minimal siphon (S2) contained in the generic siphon
        // with no place constraints and we check if S contains S2
        SetOfPlaces S2 = new SetOfPlaces(Ptilde.size());
        S2 = findMinimalSiphon(G, Stilde, new SetOfPlaces(Ptilde.size()));
        if (!S.containsSet(S2)) {
            // S is minimal!
            E.add(S.getPlaces());
        }

        // Step 7
        SetOfPlaces Pnew = S.minus(Ptilde); //Pnew = S - P~
        SetOfPlaces Pold = new SetOfPlaces(S.size()); // Pold = {}

        // Step 8
        // Decompose the problem, and for each sub-problem (corresponding to a
        // specific sub-net and some place constraints) apply the same procedure
        // from Step 1.
        PetriNet Gp;
        Vector<boolean[]> Ep;
        while (!Pnew.isEmpty()) {

            //Step 9
            int place = Pnew.removeTransition();
            Gp = reduceNet(G, G.P.getPlacesMinus(place));
            Ep = findAllMinimalSiphons(Gp, Ptilde.union(Pold));  //递归
            E.addAll(Ep);
            Pold.add(place);
        }
        return E;
    }


    /**
     * findSiphon()
     * Finds a generic siphon in a givenPetri Net G. This siphon will contain the
     * given set of places Ptilde.
     *
     * @param G      A Petri Net
     * @param Ptilde A place constraint for the resultant siphon
     * @returns A generic siphon which contains the given set of places
     */
    private SetOfPlaces findSiphon(PetriNet G, final SetOfPlaces Ptilde) {

        do {
            boolean[] placePreSet;

            // Step 1
            // check if exists a place that is element of the union of P and Ptilde
            // such that exists a transition t in its pre-set that is not an element
            // of P's post-set
            for (int place = 0; place < G.P.size(); place++) {
                if (G.P.contains(place) && Ptilde.contains(place)) {
                    // check each place that belongs to P and to ~P
                    placePreSet = G.getPlacePreSet(place);
                    for (int transition = 0; transition < placePreSet.length; transition++) {
                        if (placePreSet[transition] && !G.PPostSetcontains(transition)) {
                            // There are no possible siphons that contain Ptilde, so
                            // findSiphon ends with empty outcome
//	                     System.out.println("findSiphon returns an empty siphon");//dbg
                            return new SetOfPlaces(Ptilde.size(), false);
                        }
                    }
                }
            }

            // Step 2
            // check if there is a place which can be discarded
            int placeToEliminate = eliminablePlace_FS(G, Ptilde);
            if (placeToEliminate != -1) {
                // Step 3
                // perform the Petri Net reduction and go to Step 1
                G = reduceNet(G, G.P.getPlacesMinus(placeToEliminate));
            } else {
//	            G.P.debug("findSiphon result is ");//dbg
                return new SetOfPlaces(G.P);
            }
        } while (true);
    }


    // Step 2) of algorithm "FindSiphon"
    private int eliminablePlace_FS(final PetriNet G, final SetOfPlaces Ptilde) {
        boolean[] placePreSet;

        // check if exists a place that is element of P minus Ptilde
        // such that exists a transition t in its pre-set that is not an element
        // of P's post-set
        for (int place = 0; place < Ptilde.size(); place++) {
            if (G.P.contains(place) && !Ptilde.contains(place)) {
                placePreSet = G.getPlacePreSet(place);
                for (int transition = 0; transition < placePreSet.length; transition++) {
                    if (placePreSet[transition] && !G.PPostSetcontains(transition)) {
                        return place; //
                    }
                }
            }
        }
        return -1; // no place can be eliminated
    }


    /**
     * findMinimalSiphon()
     * Computes a mininal siphon in Petri Net G such that is contained in Stilde
     * and contains Ptilde, if exists.
     *
     * @param G      A Petri Net
     * @param Stilde A general siphon
     * @param Ptilde A set of places that the minimal siphon must contain
     * @return A minimal siphon
     */
    private SetOfPlaces findMinimalSiphon(PetriNet G, final SetOfPlaces Stilde,
                                          final SetOfPlaces Ptilde) {
        SetOfPlaces StildeCopy = new SetOfPlaces(Stilde);
        int placeToEliminate;
        //  boolean[] placePostSet;
        // boolean[] transitionPostSet;
        // boolean[] transitionPreSet;

        // Step 1
        do {
            placeToEliminate = eliminablePlace_FMS(G, StildeCopy, Ptilde);
            if (placeToEliminate != -1) {
                // Step 2
                // placeToEliminate can be removed from the given siphon
                StildeCopy.remove(placeToEliminate);
            }
        } while (placeToEliminate != -1);

        do {

            // Step 3
            if (G.P.containsSet(StildeCopy)) {
                G = reduceNet(G, StildeCopy.getPlaces());
            }

            // Step 4
            SetOfPlaces Pnew = G.P.minus(Ptilde);

            int newPlaceToEliminate;
            PetriNet Gp;
            SetOfPlaces Sp;

            do {
                // Step 5
                if (Pnew.isEmpty()) {
//	               StildeCopy.debug("[FIND_MINIMAL_SIPHON] ~S"); //dbg
                    return StildeCopy;
                }

                // Step 6
                newPlaceToEliminate = Pnew.removeTransition();
                Gp = reduceNet(G, G.P.getPlacesMinus(newPlaceToEliminate));
                Sp = findSiphon(Gp, Ptilde);
            } while (Sp.isEmpty());
            StildeCopy = Sp;
        } while (true);
    }


    // Step 1) of algorithm "FindMinimalSiphon"
    // returns the index of a place that can be eliminated or -1 if there is no
    // such place
    private int eliminablePlace_FMS(final PetriNet G, final SetOfPlaces Stilde,
                                    final SetOfPlaces Ptilde) {

        //  int placeToEliminate = -1;
        boolean[] placePostSet;
        boolean[] transitionPostSet;
        boolean[] transitionPreSet;

        for (int place = 0; place < Ptilde.size(); place++) {
            if (G.P.contains(place) && !Ptilde.contains(place) &&
                    Stilde.contains(place)) {
                // place 'place' is an element of the set P minus Ptilde
                placePostSet = G.getPlacePostSet(place);
                boolean eliminable = true;
                for (int transition = 0; transition < placePostSet.length; transition++) {
                    if (placePostSet[transition]) {
                        transitionPreSet = G.getTransitionPreSet(transition);
                        transitionPostSet = G.getTransitionPostSet(transition);

                        boolean containsCurrenPlace = false;
                        if ((transitionPreSet[place] == true) && Stilde.contains(place)) {
                            for (int currentPlace = 0; currentPlace < transitionPreSet.length; currentPlace++) {
                                if ((transitionPreSet[currentPlace] == true) &&
                                        Stilde.contains(currentPlace) &&
                                        currentPlace != place) {
                                    // transition pre-set intersection Stilde contains
                                    // place 'place'
                                    containsCurrenPlace = true;
                                    break;
                                }
                            }
                        }

                        boolean tPostSetIntersectionStildeIsEmpty = true;
                        for (int currentPlace = 0; currentPlace < transitionPostSet.length; currentPlace++) {
                            if (transitionPostSet[currentPlace] &&
                                    Stilde.contains(currentPlace)) {
                                tPostSetIntersectionStildeIsEmpty = false;
                                // transition post-set intersection Stilde is not empty
                                break;
                            }
                        }

                        if (!containsCurrenPlace &&
                                !tPostSetIntersectionStildeIsEmpty) {
                            // place is not eliminable
                            eliminable = false;
                            break;
                        }
                    }
                }
                if (eliminable == true) {
                    return place; // eliminable place
                }
            }
        }
        return -1; // there is no eliminable place
    }


    /**
     * reduceNet()
     * Simplifies a given PetriNet G discarding all places not in Ptilde and the
     * arcs connected with them.
     *
     * @param G      A Petri Net
     * @param Ptilde A set of places
     * @returns A simplified Petri Net
     */
    private PetriNet reduceNet(final PetriNet G, final boolean[] Ptilde) {
        PetriNet Gtilde = new PetriNet(G); // result

        int transitionCount = G.T.size();
        boolean[] transitionPreSet;
        boolean[] transitionPostSet;

        // for each transition in T, check if it can be discarded
        for (int transition = 0; transition < transitionCount; transition++) {
            if (G.T.contains(transition)) {
                transitionPreSet = G.getTransitionPreSet(transition);
                transitionPostSet = G.getTransitionPostSet(transition);
                boolean remove = true;
                for (int place = 0; place < Ptilde.length; place++) {
                    if ((transitionPreSet[place] || transitionPostSet[place]) &&
                            Ptilde[place]) {
                        // the intersection Ptilde and the union of the transition's
                        // pre-set and the transition's post-set is not empty, so this
                        // transition can't be discarded
                        remove = false;
                        break;
                    }
                }
                if (remove) {
                    Gtilde.T.remove(transition);
                }
            }
        }

        // discard each place p that isn't an element of the set of places Ptilde
        // and its connecting arcs.
        for (int place = 0; place < Ptilde.length; place++) {
            if (Ptilde[place] == false) {
                Gtilde.reduce(place);
            }
        }

//	      System.out.println("\nreduceNet:"); //dbg
//	      Gtilde.debug(); //dbg
        return Gtilde;
    }
	   
/*	      
	   // used for debug
	   private void print(String string, boolean[] b) {
	      System.out.println(string);
	      for (int i = 0; i < b.length; i++) {
	         System.out.print(b[i] + " ");
	      }
	      System.out.println();
	   }
*/

    // helper class.

    private class PetriNet {
        SetOfPlaces P;                     // set of places
        SetOfTransitions T;                // set of transitions
        PNMatrix forwardsIncidenceMatrix;  // input incidence matrix
        PNMatrix backwardsIncidenceMatrix; // output incidence matrix
        boolean[] PPostSet;                // union of the post-set of each place


        // constructor
        private PetriNet(PNMatrix _forwardsIncidenceMatrix,
                         PNMatrix _backwardsIncidenceMatrix) {
            forwardsIncidenceMatrix = _forwardsIncidenceMatrix.copy();
            backwardsIncidenceMatrix = _backwardsIncidenceMatrix.copy();
            P = new SetOfPlaces(forwardsIncidenceMatrix.getRowDimension(), true);
            T = new SetOfTransitions(forwardsIncidenceMatrix.getColumnDimension(), true);
            PPostSet = computePPostSet(P, T, forwardsIncidenceMatrix);
        }
	      
/*	      
	      // constructor
	      public PetriNet(int[][] _forwardsIncidenceMatrix,
	              int[][] _backwardsIncidenceMatrix){
	         forwardsIncidenceMatrix = new PNMatrix(_forwardsIncidenceMatrix);
	         backwardsIncidenceMatrix = new PNMatrix(_backwardsIncidenceMatrix);
	         P = new SetOfPlaces(forwardsIncidenceMatrix.getRowDimension(), true);
	         T = new SetOfTransitions(forwardsIncidenceMatrix.getColumnDimension(), true);
	         PPostSet = computePPostSet(P, T, forwardsIncidenceMatrix);
	      }      
*/

        // constructor
        private PetriNet(PetriNet G) {
            this(G.forwardsIncidenceMatrix, G.backwardsIncidenceMatrix);
        }


        // returns the index of a place which its pre-set is empty
        private int placeWithEmptyInputSet() {
            boolean[] placePreSet;
            boolean hasEmptyPreSet;

            for (int place = 0; place < P.size(); place++) {
                if (!P.contains(place)) {
                    continue;
                }
                placePreSet = this.getPlacePreSet(place);
                hasEmptyPreSet = true;
                for (int i = 0; i < placePreSet.length; i++) {
                    if (placePreSet[i]) {
                        hasEmptyPreSet = false;
                        break;
                    }
                }
                if (hasEmptyPreSet) {
                    return place;
                }
            }
            return -1;
        }


        // returns the pre-set of a given place
        private boolean[] getPlacePreSet(int place) {
            int[] column = forwardsIncidenceMatrix.getColumn(place);
            boolean[] result = new boolean[column.length];

            for (int i = 0; i < column.length; i++) {
                result[i] = (column[i] > 0);
            }
            return result;
        }


        // returns the post-set of a given place
        private boolean[] getPlacePostSet(int place) {
            int[] column = backwardsIncidenceMatrix.getColumn(place);
            boolean[] result = new boolean[column.length];

            for (int i = 0; i < column.length; i++) {
                result[i] = (column[i] > 0);
            }
            return result;
        }


        // returns the pre-set of a given transition
        private boolean[] getTransitionPreSet(int transition) {
            int[] row = backwardsIncidenceMatrix.getRow(transition);
            boolean[] result = new boolean[row.length];

            for (int i = 0; i < row.length; i++) {
                result[i] = (row[i] > 0);
            }
            return result;
        }


        // returns the post-set of a given transition
        private boolean[] getTransitionPostSet(int transition) {
            int[] column = forwardsIncidenceMatrix.getRow(transition);
            boolean[] result = new boolean[column.length];

            for (int i = 0; i < column.length; i++) {
                result[i] = (column[i] > 0);
            }
            return result;
        }


        // return the union of each transition post-set
        private boolean PPostSetcontains(int transition) {
            return PPostSet[transition];
        }


        // removes a place from P and clears its columns in forwards and backwards
        // incidence matrices
        private void reduce(int place) {
            P.remove(place);
            forwardsIncidenceMatrix.clearColumn(place);
            backwardsIncidenceMatrix.clearColumn(place);
            // P's post-set must be computed again!
            PPostSet = computePPostSet(P, T, backwardsIncidenceMatrix);
        }


        // computes the union of each transition post-set
        private boolean[] computePPostSet(SetOfPlaces P, SetOfTransitions T,
                                          PNMatrix forwardsIncidenceMatrix) {
            boolean[] result = new boolean[T.size()];

            for (int i = 0; i < result.length; i++) {
                result[i] = false;
            }

            for (int transition = 0; transition < result.length; transition++) {
                result[transition] = false;
                for (int place = 0; place < P.size(); place++) {
                    if (forwardsIncidenceMatrix.get(place, transition) > 0) {
                        result[transition] = true;
                        break;
                    }
                }
            }
            return result;
        }
	      
/*	      
	      // prints info for debug
	      private void debug() {
	         P.debug("P");
	         T.debug("T");
	         System.out.println("");
	         System.out.print("Forwards Incidence Matrix");
	         forwardsIncidenceMatrix.print(
	                 forwardsIncidenceMatrix.getColumnDimension(), 0);
	         System.out.print("Backwards Incidence Matrix");
	         backwardsIncidenceMatrix.print(
	                 backwardsIncidenceMatrix.getColumnDimension(), 0);
	         
	         System.out.print("P PostSet = { ");
	         for (int i = 0; i < PPostSet.length; i++) {
	            System.out.print(PPostSet[i]? i + " " : "");
	         }
	         System.out.println("}");
	      }
*/

    }


    // helper class.

    private class SetOfPlaces {
        boolean[] P; // set of places


        // constructor
        SetOfPlaces(int length) {
            this(length, false);
        }


        // constructor
        SetOfPlaces(int lenght, boolean flag) {
            P = new boolean[lenght];

            for (int place = 0; place < lenght; place++) {
                P[place] = flag;
            }
        }


        // constructor
        SetOfPlaces(SetOfPlaces set) {
            P = new boolean[set.size()];

            for (int place = 0; place < P.length; place++) {
                P[place] = set.P[place];
            }
        }


        // returns true if each element of P is false
        private boolean isEmpty() {
            for (int j = 0; j < P.length; j++) {
                if (P[j] != false) {
                    return false;
                }
            }
            return true;
        }


        // sets to true position t of P
        private void add(int t) {
            P[t] = true;
        }


        // returns a copy of P
        private boolean[] getPlaces() {
            return P.clone();
        }


        // returns a copy of P with position i set to false
        private boolean[] getPlacesMinus(int i) {
            boolean[] result = P.clone();
            result[i] = false;
            return result;
        }


        // returns a set of places S so that S = P - Ptilde
        private SetOfPlaces minus(SetOfPlaces Ptilde) {
            SetOfPlaces result = new SetOfPlaces(P.length);
            for (int i = 0; i < result.size(); i++) {
                result.P[i] = P[i];
                if (Ptilde.P[i]) {
                    result.P[i] = false;
                }
            }
            return result;
        }


        // returns index of the first position of P that is true and removes it
        // from P; if P is empty, returns -1
        private int removeTransition() {
            for (int place = 0; place < P.length; place++) {
                if (P[place] == true) {
                    P[place] = false;
                    return place;
                }
            }
            return -1;
        }


        // returns a set of places S so that S = P U Pold
        private SetOfPlaces union(SetOfPlaces Pold) {
            SetOfPlaces result = new SetOfPlaces(Pold.size());
            for (int i = 0; i < result.size(); i++) {
                result.P[i] = P[i] || Pold.P[i];
            }
            return result;
        }


        // sets to false position i of P
        private void remove(int i) {
            P[i] = false;
        }


        // returns size of P
        private int size() {
            return P.length;
        }


        //returns true if position i of P is true
        private boolean contains(int i) {
            return P[i];
        }


        // returns true if P strictly contains Stilde
        private boolean containsSet(SetOfPlaces Stilde) {
            boolean containsSet = false;
            for (int place = 0; place < P.length; place++) {
                if (Stilde.contains(place) && !this.contains(place)) {
                    return false;
                }
                if (!Stilde.contains(place) && this.contains(place)) {
                    containsSet = true; //OK, Stilde and this are not equal
                }
            }
            return containsSet;
        }
	      
/*	      
	      // used for debug purposes
	      private void debug(String s) {
	         System.out.print(s + " = { ");
	         for (int j = 0; j < P.length; j++) {
	            System.out.print(P[j]? j + " " : "");
	         }
	         System.out.println("}");
	      }
*/
    }


    // helper class.

    private class SetOfTransitions {
        boolean[] T; // set of places


        // constructor
        SetOfTransitions(int length, boolean flag) {
            T = new boolean[length];

            for (int j = 0; j < length; j++) {
                T[j] = flag;
            }
        }


        // returns size of T
        private int size() {
            return T.length;
        }


        //returns true if position i of T is true
        private boolean contains(int i) {
            return T[i];
        }


        // sets to false position i of T
        private void remove(int transition) {
            T[transition] = false;
        }
	      
/*	      
	      // used for debug purposes
	      private void debug(String s) {
	         System.out.print(s + " = { ");
	         for (int j = 0; j < T.length; j++) {
	            System.out.print(T[j]? j + " " : "");
	         }
	         System.out.println("}");
	      }
*/
    }


}
