# CMPU2001 Assignment
This assignment includes an implementation of DF and BF traveral and Prim's and Kruskal's MST algorithms

## Large Graph Data
This repo includes a Python script which allows you to download a road network from Open Street Map in the format required for these algorithms to process.
### Requirements
```sh
pip install networkx osmnx
```

## Read Before Using
For the assignment spec, screenshots of code output must be made for the report. It may be advised to change the `toChar()` methods in both files to screenshot the output with vertices as letters, not numbers. This version uses numbers to work better with larger datasets.

<table>
<tr>
<td> Using Letters </td> <td> Using Numbers </td>
</tr>
<tr>
<td>

```java
public char toChar(int u) {
    return (char) (u + 64);
}
```

</td>
<td>

```java
public String toChar(int u) {
    return Integer.toString(u);
}
```

</td>
</tr>
</table>
