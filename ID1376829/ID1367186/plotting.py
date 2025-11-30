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

    # === TITLE MOVED TO BOTTOM ===
    # y=-0.25 puts the title below the X-axis label
    plt.title(title, fontsize=14, y=-0.25)
    
    # Labels
    plt.xlabel('Dictionary Size (N)', fontsize=12)
    plt.ylabel('Memory Usage (MB)', fontsize=12)
    
    # === LEGEND BACK TO NORTH WEST ===
    # loc='upper left' places it inside the box, top-left corner
    plt.legend(loc='upper left')
    
    # Axis Limits
    plt.xlim(-x_buf, x_lim)
    plt.ylim(-y_buf, y_lim)
    
    plt.ticklabel_format(style='plain', axis='both', useOffset=False)
    plt.grid(True, linestyle='--', alpha=0.7)
    
    # Save as JPEG (bbox_inches='tight' ensures the bottom title isn't cut off)
    plt.savefig(output_filename, dpi=300, bbox_inches='tight', pad_inches=0.1)
    plt.close()
    print(f"Created {output_filename}")

# --- Main Execution ---

files = [
    ('results_fixed_7.txt', 'Fixed Word Length: 7'),
    ('results_fixed_10.txt', 'Fixed Word Length: 10'),
    ('results_fixed_31.txt', 'Fixed Word Length: 31'),
    ('results_variable.txt', 'Normally Distributed Word Lengths')
]

data_store = {}
all_mem_values = []
all_n_values = []

# Read all data
for fname, title in files:
    n, t, c = read_data(fname)
    if n:
        data_store[fname] = (n, t, c, title)
        all_mem_values.extend(t + c)
        all_n_values.extend(n)

# Global Limits
if all_mem_values and all_n_values:
    global_max_y = max(all_mem_values) * 1.05
    global_max_x = max(all_n_values) * 1.05
    
    x_pad = global_max_x * 0.02
    y_pad = global_max_y * 0.02

    # Generate Plots
    for fname, (n, t, c, title) in data_store.items():
        clean_name = fname.replace("results_", "").replace(".txt", "")
        out_name = "plot_" + clean_name + ".jpg"
        
        create_plot(n, t, c, 
                    title, 
                    out_name, 
                    global_max_x, global_max_y, x_pad, y_pad)