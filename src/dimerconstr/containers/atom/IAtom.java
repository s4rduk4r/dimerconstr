/**
 *	Atom interface
 */
package dimerconstr.containers.atom;

import dimerconstr.aux.math.Vector3d;
import dimerconstr.containers.molecule.IMolecule;

public interface IAtom {
	//Get owner molecule
	public IMolecule getMolecule();
	//Set owner molecule
	public void setMolecule(IMolecule molecule);
	//Get intramolecular name
	public String getName();
	//Get atomic index
	public Integer getIndex();
	//Get van der Waals radius
	public Double getVanDerWaalsRadius();
	//Get atomic coordinates
	public Vector3d getPosition();
	//Set atomic coordinates
	public void setPosition(Vector3d position);
	//Get donor-acceptor marker (DAM)
	public EDonorAcceptorMarker getDonorAcceptorMarker();
}
