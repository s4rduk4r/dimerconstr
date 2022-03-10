/**
 *	H-Bond descriptor
 */
package dimerconstr.containers.bond;

import dimerconstr.containers.atom.IAtom;

public class HBond implements IHBond {
	/*
	 *	Properties
	 */
	//Acceptor
	protected IAtom acceptor = null;
	//Donor
	protected IAtom donor = null;
	//Rmin = Rvw1 + Rvw2, Angstrom
	protected Double Rmin = null;
	//Rmax, Angstrom
	protected static final Double Rmax = 3.0d; 
	
	/*
	 *	Interface
	 */
	//IHBond
	//Get acceptor
	@Override
	public IAtom getAcceptor() {
		return acceptor;
	}
	//Get donor
	@Override
	public IAtom getDonor() {
		return donor;
	}
	//Get Rmin = Rvw1 + Rvw2
	@Override
	public Double getRmin() {
		if(null == Rmin)
			Rmin = acceptor.getVanDerWaalsRadius() + donor.getVanDerWaalsRadius();
		return Rmin;
	}
	//Get Rmax
	@Override
	public Double getRmax() {
		return Rmax;
	}
	
	/*
	 *	Constructors
	 */
	//Initialization constructor
	public HBond(IAtom atom1, IAtom atom2) 
	{
		this.acceptor = atom1;
		this.donor = atom2;
	}
}
