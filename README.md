# Ribosomal Simulation: COL5A1 Protein Synthesis

**Overview:** This project is a high-performance computational tool designed to simulate the ribosomal translation process, specifically targeting the **COL5A1 gene** associated with **Classic Ehlers-Danlos Syndrome (cEDS)**.

## 🎯 The Mission
Beyond a simple parser, this tool was built as part of a personal research effort to understand protein truncation. By simulating how specific genetic "roadblocks" occur, the goal is to visualize the structural collagen deficiencies that characterize EDS. My focus is to bridge the gap between computational logic and biomedical research to contribute toward finding a cure.

## 🛠 Technical Stack & Architecture
* **Language:** Java (JDK 17+)
* **Architecture:** Object-Oriented Design for molecular entities (mRNA, Ribosome, tRNA, Amino Acids).
* **Why Java?:** I chose Java to leverage its robust Type System, allowing for a clean representation of biological entities while maintaining the performance required for complex sequence analysis.

## 🚀 Key Features
* **Genomic Data Parsing:** Efficiently processes raw DNA/RNA sequences from text files with a minimal memory footprint.
* **Optimized Codon Mapping:** Implemented using a `HashMap` structure to ensure $O(1)$ lookup time, prioritizing execution speed.
* **Memory Management:** Designed with state-cleanup logic; once a termination codon is identified, the system clears transient data to keep the JVM heap optimized.
* **Mutation Analysis:** Comparison between Wild-Type (WT) and Mutant sequences to demonstrate how nonsense mutations result in non-functional, truncated proteins.

## 🧬 Scientific Logic & Complexity
* **Ribosomal Walk:** Step-by-step simulation of translation initiation, elongation, and termination.
* **Time Complexity:** Maintains $O(n)$ complexity relative to the sequence length.
* **Accuracy:** Handles premature stop codons (e.g., at position 255), providing data-driven representation of collagen deficiency.

## 👩‍💻 About the Author
**Lizbeth Sanchez Zambrano**
Senior Software Engineer with 15+ years of experience in systems architecture. Currently pivoting technical expertise toward **Biomedical Engineering**, driven by a personal commitment to solving the mysteries of Ehlers-Danlos Syndrome through code.
