package dimerconstr.constructor.aux;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import dimerconstr.app.jobs.IJobDescriptor;
import dimerconstr.aux.math.Vector3d;
import dimerconstr.aux.periodicTableOfElements.IUPACPeriodicTableOfElements;
import dimerconstr.containers.atom.IAtom;
import dimerconstr.containers.bond.IHBond;
import dimerconstr.containers.molecule.DimerComplex;
import dimerconstr.containers.molecule.IDimerComplex;
import dimerconstr.containers.molecule.IMolecule;
import dimerconstr.log.ILogger;
import dimerconstr.log.LoggerThread;
import dimerconstr.storage.IStorage;
import dimerconstr.storage.StorageThread;

public class Dimer2 extends Thread implements IDimer {
	/*
	 * Properties
	 */
	//FIXME DEBUG variables
	protected boolean isDebugFileCreated = false;
	protected long count = 0;
	//Stationary monomer
	protected IMolecule mS = null;
	//Moveable monomer
	protected IMolecule mM = null;
	//Job name
	protected String jobName = null;
	//H-bonds
	protected List<IHBond> bonds = null;
	protected IHBond[] bond = new IHBond[2];
	//Constructed complexes
	protected Map<String, List<IMolecule>> complexes = null;
	//Storage
	IStorage storage = StorageThread.getInstance();
	//Logger
	ILogger logger = LoggerThread.getInstance();
	
	/*
	 *	In-class constants
	 */
	//DEBUG constants
	protected final String DEBUG_FILE = "debug.txt";
	//Step constants
	//Everywhere applies principle of fine adjustment
	protected final double r0 = 0.0d;
	protected final double rMIN = 2 * IUPACPeriodicTableOfElements.getInstance().getAtom(IUPACPeriodicTableOfElements.H).radius;
	protected final double dR = 0.05d;
	protected final double dRho = 0.1d;
	protected final double dPhi = Math.toRadians(4.0d);
	protected final double PHI_MIN = 0.0d;
	protected final double PHI_MAX = 2.0d * Math.PI;
	protected final double dKsi = Math.toRadians(4.0d);
	protected final double KSI_MIN = -Math.PI / 4.0d;
	protected final double KSI_MAX = Math.PI / 4.0d; 
	protected final double dTheta = Math.toRadians(2.0d);
	protected final double THETA_MIN = -Math.PI / 6.0d;
	protected final double THETA_MAX = Math.PI / 6.0d;
	//Valid angles, radians
	protected final double ANGLE_PLANAR_MIN = Math.toRadians(-10.0d);
	protected final double ANGLE_PLANAR_MID = Math.toRadians(0.0d);
	protected final double ANGLE_PLANAR_MAX = Math.toRadians(10.0d);
	protected final double ANGLE_NORMAL_MIN = Math.toRadians(80.0d);
	protected final double ANGLE_NORMAL_MID = Math.toRadians(90.0d);
	protected final double ANGLE_NORMAL_MAX = Math.toRadians(100.0d);
	protected final double ANGLE_NORMAL_MMIN = Math.toRadians(-100.0d);
	protected final double ANGLE_NORMAL_MMID = Math.toRadians(-90.0d);
	protected final double ANGLE_NORMAL_MMAX = Math.toRadians(-80.0d);
	
	/*
	 *	//FIXME Debug methods
	 */
	public void DEBUGprintMs(boolean name) {
		try {
			FileWriter file = new FileWriter("Ms_" + mS.getName());
			Iterator<IAtom> atoms = mS.getAtoms();
			if(name) {
				file.write(mS.getName() + "\n");
			}
			while(atoms.hasNext())
			{
				IAtom atom = atoms.next();
				Vector3d pos = atom.getPosition();
				file.write(String.format("%s\t%.6f\t%.6f\t%.6f\n", atom.getName(), pos.x, pos.y, pos.z));
			}
			file.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	public void DEBUGprintMm(boolean name) {
		try {
			FileWriter file = new FileWriter("Mm_" + mM.getName());
			Iterator<IAtom> atoms = mM.getAtoms();
			if(name) {
				file.write(mM.getName() + "\n");
			}
			while(atoms.hasNext())
			{
				IAtom atom = atoms.next();
				Vector3d pos = atom.getPosition();
				file.write(String.format("%s\t%.6f\t%.6f\t%.6f\n", atom.getName(), pos.x, pos.y, pos.z));
			}
			file.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	public void DEBUGprintComplex(double r, double rho, double phi, double ksi, double theta) {
		try {
			String format = "%s\t%.6f\t%.6f\t%.6f\n";
			String filename = DEBUG_FILE;
			String name = String.format("%d_%.2f_%.2f_%.2f_%.2f_%.2f\n",
										++count,
										r,
										rho,
										Math.toDegrees(phi),
										Math.toDegrees(ksi),
										Math.toDegrees(theta));
			FileWriter file = new FileWriter(filename, isDebugFileCreated);
			if(!isDebugFileCreated) isDebugFileCreated = true;
			file.write(name);
			//Ms atoms
			Iterator<IAtom> atoms = mS.getAtoms();
			while(atoms.hasNext())
			{
				IAtom atom = atoms.next();
				Vector3d pos = atom.getPosition();
				file.write(String.format(format, atom.getName(), pos.x, pos.y, pos.z));
			}
			//Mm atoms
			atoms = mM.getAtoms();
			while(atoms.hasNext())
			{
				IAtom atom = atoms.next();
				Vector3d pos = atom.getPosition();
				file.write(String.format(format, atom.getName(), pos.x, pos.y, pos.z));
			}
			file.close();
		} catch (IOException e) { e.printStackTrace(); }
	}	
	
	/*
	 * Interface
	 */
	//Thread entry point
	@Override
	public void run() {
		construct();
	}
	//Construct dimer
	@Override
	public void construct() {
		//For every bond pair - perform search
		Iterator<IHBond> i = bonds.iterator();
		//Set search necessity flag
		boolean notNecessaryToSearchFurther = false;
		while(i.hasNext())
		{//CYCLE_BONDS_START
			//Set bond pair
			bond[0] = i.next();
			bond[1] = i.next();
			logger.log(String.format("Processing bonds: %s", getHBondsName()));
			//Define Rmax and Rmin
			Double Rmin = bond[0].getRmin() > bond[1].getRmin() ? bond[1].getRmin() : bond[0].getRmin();
			Double Rmax = bond[0].getRmax() < bond[1].getRmax() ? bond[1].getRmax() : bond[0].getRmax();
			//Reorient Ms along it's active atoms
			IAtom[] activeAtomsMs = new IAtom[2];
			for(int j = 0; j < 2; ++j)
			{
				activeAtomsMs[j] = bond[j].getAcceptor().getMolecule() == mS 
									? bond[j].getAcceptor() 
									: bond[j].getDonor();
			}
			mS.resetOrientation();
			mS.reorientAlong(activeAtomsMs[0], activeAtomsMs[1], true);
			//DEBUGprintMs(true); //FIXME DEBUGprintMs 
			//Prepare Mm
			//Reorient Mm along it's active atoms
			for(int j = 0; j < 2; ++j)
			{
				activeAtomsMs[j] = bond[j].getAcceptor().getMolecule() == mM
									? bond[j].getAcceptor() 
									: bond[j].getDonor();
			}
			mM.resetOrientation();
			mM.reorientAlong(activeAtomsMs[0], activeAtomsMs[1], false);
			//DEBUGprintMm(true); //FIXME DEBUGprintMm
			//Set complex type to construct
			EComplexType type = EComplexType.PLANAR;
			while(type != EComplexType.NONE)
			{//CYCLE_CONSTRUCTION_START
				//Reset Mm position
				mM.rotX(0.0d);
				mM.rotY(0.0d);
				mM.rotZ(0.0d);
				mM.setPosition(0.0d, r0, 0.0d);//dimerconstr-0.run //FIXME mM.setPosition(0.0d, rMin, 0.0d);
				Double r = mM.getPosition().y;
				notNecessaryToSearchFurther = false;
				while(r < Rmax)
				{//CYCLE_R_START
					//Break search if it's not necessary to do so for this bond pair
					if(notNecessaryToSearchFurther)
						break;
					//Reset all parameters
					//Calculate RHO_MIN and RHO_MAX
					final Double RHO_MIN = Math.sqrt(Rmin * Rmin - rMIN * rMIN);
					final Double RHO_MAX = Math.sqrt(Rmax * Rmax - r * r);
					Double rho = RHO_MIN;
					Double phi = PHI_MIN;
					Double ksi = KSI_MIN;
					Double theta = THETA_MIN; 
					while(rho < RHO_MAX)
					{//CYCLE_RHO_START
						if(notNecessaryToSearchFurther)
							break;
						if(EComplexType.PLANAR != type)
						{//NON-PLANAR_COMPLEXES_START
							while(phi < PHI_MAX)
							{//CYCLE_PHI_START
								if(notNecessaryToSearchFurther)
									break;
								//{X, Z} on current search disk
								Vector3d diskPos = new Vector3d(rho * Math.cos(phi), r, rho * Math.sin(phi));
								while(ksi < KSI_MAX)
								{//CYCLE_KSI_START
									if(notNecessaryToSearchFurther)
										break;
									mM.rotY(ksi);
									while(theta < THETA_MAX)
									{//CYCLE_THETA_START
										if(notNecessaryToSearchFurther)
											break;
										mM.rotZ(theta);
										mM.setPosition(diskPos);
										//Set Mm to NORMAL_POSITIVE or NORMAL_NEGATIVE types
										mS.rotX(EComplexType.NORMAL_POS == type ? Math.PI / 2.0d : -Math.PI / 2.0d);
										//FIXME DEBUGprintComplex(r, rho, phi, ksi, theta);
										if(!isMonomersIntersection()) {
											if(isComplexCreated()) {
												if(checkDonorAcceptorDistances()) {
													String complexName = getComplexName();
													complexName += EComplexType.NORMAL_POS == type ? "-n" : "-nm" ;
													addFoundComplexes(complexName, 0.0d);
													//Store complex
													storeResults();
													complexes.clear();
													notNecessaryToSearchFurther = true;
												}
											}
										}
										//Change THETA
										theta += dTheta;
									}//CYCLE_THETA_END
									//Reset THETA
									theta = THETA_MIN;
									//Change KSI
									ksi += dKsi;
								}//CYCLE_KSI_END
								//Reset KSI
								ksi = KSI_MIN;
								//Change PHI
								phi += dPhi;
							}//CYCLE_PHI_END
							//Reset phi
							phi = PHI_MIN;
						}//NON-PLANAR_COMPLEXES_END
						else {//PLANAR_COMPLEXES_START
							Vector3d diskPos = new Vector3d(rho, r, 0.0d);
							while(theta < THETA_MAX)
							{//CYCLE_THETA_START
								if(notNecessaryToSearchFurther)
									break;
								for(int k = 0; k < 2; ++k)
								{
									mM.rotZ(theta);
									if(1 == k) diskPos.x = -diskPos.x;
									mM.setPosition(diskPos);
									if(!isMonomersIntersection()) {
										if(isComplexCreated()) {
											if(checkDonorAcceptorDistances()) {
												addFoundComplexes(getComplexName() + "-p", 0.0d);
												//Store complex
												storeResults();
												complexes.clear();
												notNecessaryToSearchFurther = true;
											}
										}
									}
								}
								//Change theta
								theta += dTheta;
							}//CYCLE_THETA_END
							//Reset theta
							theta = THETA_MIN;
						}//PLANAR_COMPLEXES_END
						//Change RHO
						rho += dRho;
					}//CYCLE_RHO_END
					//Change R
					if(r0 == r) 
						r = rMIN;
					else 
						r += dR;
				}//CYCLE_R_END
				if(EComplexType.PLANAR == type) {
					type = EComplexType.NORMAL_NEG;
				} else {
					type = EComplexType.NORMAL_NEG == type ? EComplexType.NORMAL_POS: EComplexType.NONE;
				}
			}//CYCLE_CONSTRUCTION_END
			//Reset search necessity flag
			notNecessaryToSearchFurther = false;
		}//CYCLE_BONDS_END
	}
	
	//Get constructed complexes
	@Override
	public List<IMolecule> getComplexes() throws DimerNoComplexesConstructedException {
		//If no complexes found - tell about it
		if(null == complexes)
			throw new DimerNoComplexesConstructedException();
		return complexes.values().iterator().next();
	}
	
	/*
	 * Auxiliary methods
	 */
	//Define Ms and Mm molecules
	protected void defineMs(IMolecule m1, IMolecule m2) {
		if(m1.getAtomCount() >= m2.getAtomCount()) {
			this.mS = m1;
			this.mM = m2;
		} else {
			this.mS = m2;
			this.mM = m1;
		}
	}
	//Create complex name
	protected String getComplexName() {
		String[] strBond = new String[2];
		for(int i = 0; i < 2; ++i)
		{
			if(mS == bond[i].getAcceptor().getMolecule())
			{
				strBond[i] = bond[i].getAcceptor().getName() + bond[i].getDonor().getName();
			} else {
				strBond[i] = bond[i].getDonor().getName() + bond[i].getAcceptor().getName();
			}
			strBond[i] = strBond[i].toUpperCase();
		}
		return String.format("%s_%s-%s_%s", 
							 mS.getName(),
							 mM.getName(),
							 strBond[0],
							 strBond[1]
							);
	}
	//Create H-bonds name
	protected String getHBondsName() {
		String[] strBond = new String[2];
		for(int i = 0; i < 2; ++i)
		{
			if(mS == bond[i].getAcceptor().getMolecule())
			{
				strBond[i] = String.format("%s:%s", bond[i].getAcceptor().getName(), bond[i].getDonor().getName());
			} else {
				strBond[i] = String.format("%s:%s", bond[i].getDonor().getName(), bond[i].getAcceptor().getName());
			}
			strBond[i] = strBond[i].toUpperCase();
		}
		return String.format("%s %s",
							 strBond[0],
							 strBond[1]
							);
	}
	//Check if required H-bonds has been created
	protected boolean isComplexCreated() {
		boolean isCreated = true;
		for(int i = 0; i < 2; ++i)
		{
			//Get parameters of the bond
			Double rMin = bond[i].getRmin();
			Double rMax = bond[i].getRmax();
			//Get H-bond acceptor
			IAtom acceptor = bond[i].getAcceptor();
			acceptor = acceptor.getMolecule().getAtom(acceptor.getName());
			//Get H-bond donor
			IAtom donor = bond[i].getDonor();
			donor = donor.getMolecule().getAtom(donor.getName());
			//Calculate bond length
			Double distance = acceptor.getPosition().sub(donor.getPosition()).length();
			//Check if bond established
			isCreated = isCreated & (distance >= rMin) & (distance <= rMax);
			if(!isCreated) break;
		}
		return isCreated;
	}
	//Check atomic distances between atoms belonging to different monomers
	protected boolean isMonomersIntersection() {
		Iterator<IAtom> iAm = mM.getAtoms();
		while(iAm.hasNext())
		{
			IAtom aM = iAm.next();
			Vector3d posAm = aM.getPosition();
			Iterator<IAtom> iAs = mS.getAtoms();
			while(iAs.hasNext())
			{
				IAtom aS = iAs.next();
				Vector3d rAmAs = aS.getPosition().sub(posAm);
				Double rMin = aM.getVanDerWaalsRadius() + aS.getVanDerWaalsRadius();
				//If distance between Am and As less than Rmin = Rvw1 + Rvw2 - then complex is invalid
				if(rAmAs.length() < rMin)
					return true;
			}
		}
		return false;
	}
	//Check atomic distances between H-bond donors/acceptors and non-bond acceptors/donors accordingly
	protected boolean checkDonorAcceptorDistances() {
		for(int i = 0; i < 2 ; ++i)
			{
			//Define Rmax
			Double rMax = bond[i].getRmax();
			//Get H-bond donor
			IAtom bondDonor = bond[i].getDonor();
			bondDonor = bondDonor.getMolecule().getAtom(bondDonor.getName());
			//Get H-bond acceptor
			IAtom bondAcceptor = bond[i].getAcceptor();
			bondAcceptor = bondAcceptor.getMolecule().getAtom(bondAcceptor.getName());
			//Get acceptors
			Iterator<IAtom> iAcceptors = (mM == bondDonor.getMolecule()) ? mS.getAcceptors().iterator() : mM.getAcceptors().iterator();
			//Get donors
			Iterator<IAtom> iDonors = (mM == bondAcceptor.getMolecule()) ? mS.getDonors().iterator() : mM.getDonors().iterator();
			//Iterate through all acceptors to check distances between them and H-bond donor
			while(iAcceptors.hasNext())
			{
				IAtom acceptor = iAcceptors.next();
				//Skip H-bond acceptor check. Validity of this kind of distance must be performed in isComplexCreated() 
				if(bondAcceptor.getName() == acceptor.getName())
					continue;
				//Calculate distance between H-bond donor and current acceptor
				Double distance = bondDonor.getPosition().sub(acceptor.getPosition()).length();
				//If it's less than Rmax, then complex invalid
				if(distance < rMax)
					return false;
			}
			//Iterate through all donors to check distances between them and H-bond acceptor
			while(iDonors.hasNext())
			{
				IAtom donor = iDonors.next();
				//Skip H-bond donor check. Validity of this kind of distance must be performed in isComplexCreated()
				if(bondDonor.getName() == donor.getName())
					continue;
				//Calculate distance between H-bond acceptor and current donor
				Double distance = bondAcceptor.getPosition().sub(donor.getPosition()).length();
				//If it's less than Rmax, then complex invalid
				if(distance < rMax)
					return false;
			}
		}
		return true;
	}
	//Add found complexes to storage
	protected void addFoundComplexes(String complexName, Double maxBondLength) {
		//Create container for found complexes
		if(null == complexes)
			complexes = new TreeMap<String, List<IMolecule>>();
		//Create complex from Ms and Mm
		IMolecule complex = new DimerComplex(complexName, maxBondLength);
		//Add Ms atoms
		Iterator<IAtom> atoms = mS.getAtoms();
		while(atoms.hasNext())
		{
			IAtom atom = atoms.next();
			complex.addAtom(atom);
		}
		//Add Mm atoms
		atoms = mM.getAtoms();
		while(atoms.hasNext())
		{
			IAtom atom = atoms.next();
			complex.addAtom(atom);
		}
		//Store angles and radius-vector module
		complex.setPlaneAngle(mM.getPlaneAngle());
		complex.setRotationAngle(mM.getRotationAngle());
		complex.setDiskAngle(mM.getDiskAngle());
		complex.setRadiusVector(mM.getRadiusVector());
		//Storage complex
		if(!complexes.containsKey(complexName)) {
			//First time
			List<IMolecule> complexesList = new LinkedList<IMolecule>();
			complexesList.add(complex);
			complexes.put(complexName, complexesList);
		} else {
			complexes.get(complexName).add(complex);
		}
	}
	//Filter found complexes for the best
	protected void filterToBest(EComplexType type, boolean isPrefixAllowed) {
		//If no complexes found - do nothing
		if((null == complexes) || complexes.isEmpty()) {
			return;
		}
		logger.log("Complex construction complete. Filtering...", true);
		//Set MIN, MID, MAX angles to check
		final double ANGLE_MIN = EComplexType.PLANAR == type ? ANGLE_PLANAR_MIN : 
						   (EComplexType.NORMAL_NEG == type ? ANGLE_NORMAL_MMIN : ANGLE_NORMAL_MIN);
		final double ANGLE_MID = EComplexType.PLANAR == type ? ANGLE_PLANAR_MID : 
						   (EComplexType.NORMAL_NEG == type ? ANGLE_NORMAL_MMID : ANGLE_NORMAL_MID);
		final double ANGLE_MAX = EComplexType.PLANAR == type ? ANGLE_PLANAR_MAX : 
						   (EComplexType.NORMAL_NEG == type ? ANGLE_NORMAL_MMAX : ANGLE_NORMAL_MAX);
		final String prefixMin = EComplexType.PLANAR == type ? "-p1" : 
						   (EComplexType.NORMAL_NEG == type ? "-nm1" : "-n1");
		final String prefixMid = EComplexType.PLANAR == type ? "-p0" : 
						   (EComplexType.NORMAL_NEG == type ? "-nm0" : "-n0");
		final String prefixMax = EComplexType.PLANAR == type ? "-p2" : 
			   			   (EComplexType.NORMAL_NEG == type ? "-nm2" : "-n2");
		//Found complexes
		Iterator<String> names = complexes.keySet().iterator();
		while(names.hasNext())
		{
			IMolecule bestMin = null;
			IMolecule bestMid = null;
			IMolecule bestMax = null;
			String name = names.next();
			List<IMolecule> foundComplexes = complexes.get(name);
			Iterator<IMolecule> molecules = foundComplexes.iterator();
			//Filter cycle
			while(molecules.hasNext())
			{
				IMolecule complex = molecules.next();
				Double angle = complex.getPlaneAngle();
				Double complexMaxLength = ((IDimerComplex) complex).getMaxBondLength();
				//MIN angle
				if(ANGLE_MIN == angle) {
					if(null == bestMin) {
						bestMin = complex;
						continue;
					} else {
						if(complexMaxLength < ((IDimerComplex) bestMin).getMaxBondLength())
							bestMin = complex;
					}
				}
				//MID angle
				if(ANGLE_MID == angle) {
					if(null == bestMid) {
						bestMid = complex;
						continue;
					} else {
						if(complexMaxLength < ((IDimerComplex) bestMid).getMaxBondLength())
							bestMid = complex;
					}
				}
				//MAX angle
				if(ANGLE_MAX == angle) {
					if(null == bestMax) {
						bestMax = complex;
						continue;
					} else {
						if(complexMaxLength < ((IDimerComplex) bestMax).getMaxBondLength())
							bestMax = complex;
					}
				}
			}
			//Remember found complexes
			String bestMidName = null;
			String bestMinName = null;
			String bestMaxName = null;
			if(null != bestMid) {
				bestMidName = isPrefixAllowed ? bestMid.getName().concat(prefixMid) : bestMid.getName();
				bestMid = new DimerComplex(bestMidName , (DimerComplex) bestMid);
			} else {
				if(null != bestMin) {
					bestMinName = isPrefixAllowed ? bestMin.getName().concat(prefixMin) : bestMin.getName();
					bestMin = new DimerComplex(bestMinName, (DimerComplex) bestMin);
				}
				if(null != bestMax) {
					bestMaxName = isPrefixAllowed ? bestMax.getName().concat(prefixMax) : bestMax.getName();
					bestMax = new DimerComplex(bestMaxName, (DimerComplex) bestMax);
				}
			}
			//Clear complexes
			foundComplexes.clear();
			//Repopulate complexes
			if(null != bestMid) {
				foundComplexes.add(bestMid);
			} else {
				if(null != bestMin) {
					foundComplexes.add(bestMin);
				}
				if(null != bestMax) {
					foundComplexes.add(bestMax);
				}
			}
		}
		logger.log("done");
	}
	//Store results
	protected void storeResults() {
		//If no complexes found - do nothing
		if((null == complexes) || complexes.isEmpty()) {
			logger.log("Nothing to store!");
			return;
		}
		//Otherwise - store results
		Iterator<List<IMolecule>> foundComplexes = complexes.values().iterator();
		while(foundComplexes.hasNext())
		{
			Iterator<IMolecule> dimers = foundComplexes.next().iterator();
			while(dimers.hasNext())
			{
				IMolecule dimer = dimers.next();
				storage.store(dimer);
				logger.log(String.format("Stored %s", dimer.getName()));
			}
		}
	}
	
	
	/*
	 *	Constructor
	 */
	//Initialization constructor
	public Dimer2(IJobDescriptor job) {
		//Define stationary and movable monomers
		defineMs(job.getMonomer1(), job.getMonomer2());
		//Get H-bonds
		bonds = job.getHBonds();
		//Get job name
		jobName = job.getJobName();
	}
}
