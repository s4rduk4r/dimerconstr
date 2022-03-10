# dimerconstr
Dimer construction tool for quantum chemistry. Is a supplement for a scientific paper (DOI: 10.15407/dopovidi2014.08.133)

# Usage
```sh
java -jar ./dimerconstr.run inputfile1 [inputfile2] [...]
```
## Help
```sh
java -jar ./dimerconstr.run -help
```
# Input file structure

Preferred way to produce correct input is to use [hbondsgen](https://github.com/s4rduk4r/hbondsgen)

Example input file is here - https://github.com/s4rduk4r/dimerconstr/blob/main/example_input.txt

>Input file consists of commentary lines and 3 sections: `TITLE`, `GEOMETRY`, `BONDS`<br>
>Each section is terminated by an empty line.<br>
>Each commentary begins with '#' character. Example of commentary line is given below:<br>
>
>\# This is a commentary line<br>
>
> `TITLE` section consists of any number alphanumeric characters. No special characters allowed.<br>
>
> `GEOMETRY` section consists of 2 monomer descriptions.<br>
>Monomer description starts with molecule name on a separate line. Then goes any number of atomic descriptions in format:<br>
>		`IMN`	`X`	`Y`	`Z`	`DAM`<br>
>where	`IMN`- intramolecular name. E.g. N1, N3, H4-1, etc.<br>
>	`X`, `Y`, `Z` - floating point atomic coordinates in decard coordinate system<br>
>	`DAM` - donor-acceptor marker. If atom is donor - then it must be marked by 'D' or 'd' character.<br>
>If atom is acceptor - then it must be marked by 'A' or 'a' character. If atom is neither donor or acceptor - then no marker is needed<br>
>	`P` - molecule plane atom. If atom marked with 'P' or 'p' character, then it is considered as belonging to molecule plane.
>
> `BONDS` section consists of H-bonds pairs in format: `IMN1`:`IMN2` `IMN3`:`IMN4`<br>
>where	`IMN1` and `IMN3` are 1st monomer's atoms.<br>
>	`IMN2` and `IMN4` are 2nd monomer's atoms.

# Output
Output is a simple `.xyz` file
