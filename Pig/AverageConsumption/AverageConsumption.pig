data = LOAD 'file:///D:/PowerConsumption/data/household_power_consumption.csv'
USING PigStorage(',')
AS (
Date:chararray,
Time:chararray,
Global_active_power:double,
Global_reactive_power:double,
Voltage:double,
Global_intensity:double,
Sub_metering_1:double,
Sub_metering_2:double,
Sub_metering_3:double
);

clean_data = FILTER data BY Global_active_power IS NOT NULL;

with_month = FOREACH clean_data GENERATE
SUBSTRING(Date, 3, 5) AS Month,
Global_active_power;

grouped_data = GROUP with_month BY Month;

average_consumption = FOREACH grouped_data GENERATE
group AS Month,
AVG(with_month.Global_active_power) AS Average_Power;

STORE average_consumption
INTO 'file:///D:/PowerConsumption/Pig/output/AverageConsumption'
USING PigStorage('\t');