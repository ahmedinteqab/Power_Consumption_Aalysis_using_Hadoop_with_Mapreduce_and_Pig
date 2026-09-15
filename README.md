# ⚡ Household Electric Power Consumption Analysis

> A Big Data project for analyzing large-scale household electricity consumption using **Hadoop MapReduce** and **Apache Pig**.

---

## 📊 Dataset

The project uses the **Individual Household Electric Power Consumption** dataset from the **UCI Machine Learning Repository**.

🔗 **Dataset:**  
https://archive.ics.uci.edu/dataset/235/individual%2Bhousehold%2Belectric%2Bpower%2Bconsumption

- 📌 **Records:** 2,075,259
- 📌 **Features:** 9
- 📌 **Sampling Rate:** 1 minute
- 📌 **Time Period:** December 2006 – November 2010
- 📌 **Dataset Type:** Multivariate Time-Series


🗺️ MapReduce Operations
1. Maximum Power Consumption

Find the maximum value of Global_active_power.

2. Average Voltage

Calculate the average Voltage across all valid records.

3. Total Energy Consumption

Calculate the total energy consumption using the available power and sub-metering data.

🐷 Pig Operations
1. Top 10 Power Consumption

Find the 10 records with the highest Global_active_power.

2. Maximum Voltage Values

Find the 10 records with the highest Voltage values.

3. Average Consumption

Calculate the average Global_active_power.

4. High Consumption Filtering

Filter records where Global_active_power is above a selected threshold.

5. Sort by Power Consumption

Sort records in descending order according to Global_active_power.
