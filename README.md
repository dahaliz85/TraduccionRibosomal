🧬 Overview
This project is a high-performance computational tool designed to simulate the ribosomal translation process, specifically targeting the COL5A1 gene associated with Classic Ehlers-Danlos Syndrome (cEDS).

The simulator models how a nonsense mutation (premature stop codon) affects protein synthesis, providing a visual and data-driven representation of truncated collagen chains.

🛠 Technical Stack
Language: Java (JDK 17+)

Architecture: Object-Oriented Design for molecular entities (mRNA, Ribosome, tRNA, Amino Acids).

Performance: Optimized for processing large genomic sequences using efficient string handling and custom data structures.

Scientific Logic: Implements the standard genetic code table for codon-to-amino-acid mapping.

🔬 Scientific Context: Classic Ehlers-Danlos Syndrome (cEDS)
The simulation focuses on the COL5A1 gene mutation. In a healthy state, this gene produces a protein of 1,838 amino acids.

The Problem: Nonsense mutations create a premature stop codon.

The Simulation: This tool demonstrates how the ribosome stops at position 255 (or relevant clinical coordinates), resulting in a non-functional, truncated protein that leads to collagen deficiency.

🚀 Key Features
Sequence Parsing: Loads and validates raw DNA/mRNA sequences.

Ribosomal Walk: Step-by-step simulation of the translation initiation, elongation, and termination.

Mutation Analysis: Comparison between Wild-Type (WT) and Mutant sequences.

Reporting: Generates detailed logs of the resulting polypeptide chain length and composition.

📥 Installation & Usage
👩‍🔬 About the Author
Lizbeth Sanchez Zambrano Senior Software Engineer with 15+ years of experience, currently transitioning into Biomedical Engineering. This project is part of an ongoing research effort to find computational solutions and eventually a cure for Ehlers-Danlos Syndrome.
