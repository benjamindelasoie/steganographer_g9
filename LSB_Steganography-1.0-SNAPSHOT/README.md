# LSB Steganography

BMP steganography capabilities with optional encryption.

This is an academic project for the course *(72.44) - Criptografía y Seguridad* at Instituto Tecnológico de Buenos
Aires (ITBA).

## Installation

The project uses maven. The first step is to build the project. On the project's root directory run:

```bash
mvn install
```

This will generate a .tar.gz file inside the /target directory thanks to Maven's assembly plugin. This zipped file
contains the .jar packaged with all its dependencies.

You can now unzip this tar to your preferred location by running:

```bash
tar -xzf /path/to/target -C /desired/path/
```

Or simply run the script:

```bash
extract.sh
```

Which will simply unzip the tar to the parent directory of the root of your repository location.

## Usage

Standing on unzipped directory, you will find a bash script which you can run as:

```bash
./stegobmp.sh
```

You can use it as it is or make an alias for clarity.

```bash
alias stegobmp="/path/to/stegobmp.sh"
```

You can see the help message running the command:

```bash
./stegobmp.sh --help
```

At this point, you can utilize the program functionalities by running either of the subcommands.
If you want your input file to be encrypted before being embedded onto the cover file, just
provide a password. You can either submit the cipher and mode parameters or the default ones will
be used (aes128, cbc).

### Embedding

To embed any file inside a .bmp file, run:

```bash
./stegobmp.sh -embed -in=INPUT_FILE -p=COVER -out=outputFile -steg=ALGO \
  [-a=CYPHER -m=MODE -pass=PASSWORD]
```

### Extracting

If you have a bmp image which you know has data embedded on, you can extract by running the command:

```bash
./stegobmp.sh -extract -p=COVER -out=OUTPUT_NAME -steg=ALGO \
   [-a=CYPHER -m=MODE -pass=PASSWORD]
```

### Supported Algorithms

#### Steganography algorithms (-steg)

- "LSB1": standard least significant bit steganography.
- "LSB4": embeds the data in similar fashion but uses the four least significant bits. It works with smaller images but
  the embeddings distort the image to easily perceivable amounts.
- "LSBI": improved LSB algorithm that uses inversion as describe
  in [this paper](https://www.jatit.org/volumes/Vol80No2/16Vol80No2.pdf).

#### Encryption (-a)

- des
- aes128
- aes192
- aes256

#### Block mode (-m)

- ecb: Electronic Code Book
- cfb: Cipher Feedback
- cbc: Cipher Block Chaining
- ofb: Output Feedback

### Considerations

Take into account that for obvious reasons, the data extraction functions properly as long as you provide the exact same
parameters which were used for the embedding of this data in the first place.