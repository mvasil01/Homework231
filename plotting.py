import matplotlib.pyplot as plt

def read_data(filename):
    """Reads the results file and returns lists of N, TrieMB, and CompressedTrieMB."""
    n_values = []
    trie_mem = []
    comp_trie_mem = []
    try:
        with open(filename, 'r') as f:
            lines = f.readlines()
            # Skip header
            for line in lines[1:]:
                parts = line.strip().split()
                if len(parts) >= 3:
                    n_values.append(int(parts[0]))
                    # Convert bytes to MB
                    trie_mem.append(int(parts[1]) / (1024 * 1024)) 
                    comp_trie_mem.append(int(parts[2]) / (1024 * 1024))
    except FileNotFoundError:
        print(f"File {filename} not found.")
        return [], [], []
    return n_values, trie_mem, comp_trie_mem

def create_plot(n_vals, t_mem, c_mem, title, output_filename, x_lim, y_lim, x_buf, y_buf):
    if not n_vals: return
    
    plt.figure(figsize=(10, 6))
    
    # Classic Trie
    plt.plot(n_vals, t_mem, 
             marker='o', linestyle='--', color='black', 
             markerfacecolor='none', markeredgecolor='black', 
             label='Trie')
    
    # Compressed Trie
    plt.plot(n_vals, c_mem, 
             marker='s', linestyle='-', color='black', 
             markerfacecolor='black', 
             label='Compressed Trie')

    plt.title(title, fontsize=14)
    plt.xlabel('Dictionary Size (N)', fontsize=12)
    plt.ylabel('Memory Usage (MB)', fontsize=12)
    
    # === LINEAR AXIS ===
    # Use real N values for X-axis to show true linear relationship.
    # Set limits to include a small negative buffer so (0,0) isn't on the edge.
    plt.xlim(-x_buf, x_lim)
    plt.ylim(-y_buf, y_lim)
    
    # Place Legend in North West (Upper Left)
    plt.legend(loc='upper left')
    
    plt.ticklabel_format(style='plain', axis='both', useOffset=False)
    plt.grid(True, linestyle='--', alpha=0.7)
    
    # Save as JPEG with tight bounding box
    plt.savefig(output_filename, dpi=300, bbox_inches='tight', pad_inches=0.05)
    plt.close()
    print(f"Created {output_filename}")

# --- Main Execution ---
n1, t1, c1 = read_data('results_fixed_length.txt')
n2, t2, c2 = read_data('results_variable_length.txt')

# Calculate Global Limits and Buffers
all_mem = t1 + c1 + t2 + c2
all_n = n1 + n2

if all_mem and all_n:
    # Max limits with 5% top/right padding
    global_max_y = max(all_mem) * 1.05 
    global_max_x = max(all_n) * 1.05
    
    # Negative buffer (2% of range) for breathing room at origin
    x_pad = global_max_x * 0.02
    y_pad = global_max_y * 0.02

    create_plot(n1, t1, c1, 
                'Memory Comparison: Fixed Length Words', 
                'plot_fixed_length.jpg', 
                global_max_x, global_max_y, x_pad, y_pad)

    create_plot(n2, t2, c2, 
                'Memory Comparison: Variable Length Words', 
                'plot_variable_length.jpg', 
                global_max_x, global_max_y, x_pad, y_pad)