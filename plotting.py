import matplotlib.pyplot as plt

def create_plot(data_file, plot_title, output_pdf):
    n_values = []
    trie_bytes = []
    compressed_bytes = []

    # Read data from file
    try:
        with open(data_file, 'r') as f:
            # Skip the header line
            next(f)
            for line in f:
                parts = line.strip().split()
                if len(parts) < 3:
                    continue
                
                # Parse columns: N, Trie_Bytes, CompressedTrie_Bytes
                n = int(parts[0])
                t_mem = int(parts[1])
                c_mem = int(parts[2])

                n_values.append(n)
                # Convert to Megabytes (MB) for better readability
                trie_bytes.append(t_mem / (1024 * 1024))
                compressed_bytes.append(c_mem / (1024 * 1024))
    except FileNotFoundError:
        print(f"Error: Could not find file {data_file}")
        return

    # Create the Plot
    plt.figure(figsize=(10, 6))
    
    # Plot Classic Trie: "Clear Black" (Hollow Circle)
    plt.plot(n_values, trie_bytes, 
             marker='o',              # Circle shape
             linestyle='--',           # Solid line
             color='black',           # Black line
             markerfacecolor='none',  # Transparent fill ("clear")
             markeredgecolor='black', # Black outline
             label='Trie')
    
    # Plot Compressed Trie: "Full Black" (Filled Square)
    plt.plot(n_values, compressed_bytes, 
             marker='s',              # Square shape
             linestyle='-',          # Dashed line (to help distinguish lines)
             color='black',           # Black line
             markerfacecolor='black', # Black fill ("full")
             label='Compressed Trie')

    # Formatting
    plt.title(plot_title, fontsize=14)
    plt.xlabel('Dictionary Size (N)', fontsize=12)
    plt.ylabel('Memory Usage (MB)', fontsize=12)
    plt.legend()
    plt.grid(True, linestyle='--', alpha=0.7)
    
    # Save to PDF
    plt.savefig(output_pdf)
    print(f"Plot saved to {output_pdf}")
    plt.close()

# Generate Plot 1: Fixed Length
create_plot('results_fixed_length.txt', 
            'Memory Comparison: Fixed Length Words', 
            'plot_fixed_length.pdf')

# Generate Plot 2: Variable Length
create_plot('results_variable_length.txt', 
            'Memory Comparison: Variable Length Words (Normal Dist.)', 
            'plot_variable_length.pdf')