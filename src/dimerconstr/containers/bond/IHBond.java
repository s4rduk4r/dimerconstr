/**
 *	H-bond interface
 */
package dimerconstr.containers.bond;

import dimerconstr.containers.atom.IAtom;

public interface IHBond {
	//Get acceptor
	public IAtom getAcceptor();
	//Get donor
	public IAtom getDonor();
	//Get Rmin = Rvw1 + Rvw2
	public Double getRmin();
	//Get Rmax
	public Double getRmax();
}
