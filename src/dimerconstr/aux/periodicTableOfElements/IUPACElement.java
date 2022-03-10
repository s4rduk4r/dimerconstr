/**
 *	IUPAC Periodic Table of Elements element descriptor
 */
package dimerconstr.aux.periodicTableOfElements;

public class IUPACElement {
	/*
	 *	Properties
	 */
	//Atomic number
	public Integer number = null;
	//Atomic symbol
	public String symbol = null;
	//Atomic name
	public String name = null;
	//Standard atomic weight
	public Double weight = null;
	//van der Waals radius, Angstroms
	public Double radius = null;
	
	/*
	 *	Constructor
	 */
	public IUPACElement(Integer atomic_number, String atomic_symbol, String atomic_name, Double atomic_weight, Double vanDerWallsRadius) {
		this.number = atomic_number;
		this.symbol = atomic_symbol;
		this.name = atomic_name;
		this.weight = atomic_weight;
		this.radius = vanDerWallsRadius;
	}
}
