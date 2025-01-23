document.addEventListener("DOMContentLoaded", () => {
    const fetchEntries = (startDate, endDate) => {
        let url = "/food-entries/user/entries";
        if (startDate && endDate) {
            url = `/food-entries/user/date-range?startDate=${startDate}&endDate=${endDate}`;
        }

        fetch(url)
            .then(response => response.json())
            .then(data => {
                const tableBody = document.getElementById("entriesTableBody");
                tableBody.innerHTML = ""; // Clear existing rows
                data.forEach(entry => {
                    const newRow = `
                        <tr>
                            <td>${entry.name}</td>
                            <td>${entry.calories}</td>
                            <td>${entry.price.toFixed(2)}</td>
                            <td>${new Date(entry.createdAt).toLocaleDateString()}</td>
                        </tr>
                    `;
                    tableBody.insertAdjacentHTML("beforeend", newRow);
                });
            })
            .catch(error => console.error("Error fetching food entries:", error));
    };

    document.getElementById("filterButton").addEventListener("click", () => {
        const startDate = document.getElementById("startDate").value;
        const endDate = document.getElementById("endDate").value;
        fetchEntries(startDate, endDate);
    });

    // Initial fetch without date range
    fetchEntries();

    fetch("/food-entries/user/total-calories/date/" + new Date().toISOString().split("T")[0])
        .then(response => response.json())
        .then(data => {
            const totalCalories = document.getElementById("dailyCalories");
            totalCalories.textContent = data;
        })
        .catch(error => console.error("Error fetching total calories:", error));

    fetch("/food-entries/user/weekly-average/spending")
        .then(response => response.json())
        .then(data => {
            const totalPrice = document.getElementById("weeklyExpenditure");
            totalPrice.textContent = data.toFixed(2);
        })
        .catch(error => console.error("Error fetching total spending:", error));

    fetch("/food-entries/user/weekly-calories")
        .then(response => response.json())
        .then(data => {
            // Extract labels and data from the API response
            const labels = data.map(entry => entry.day); // Extract days (e.g., "Thursday")
            const calories = data.map(entry => entry.totalCalories); // Extract total calories

            // Get the chart context
            const ctx = document.getElementById('calorieChart').getContext('2d');

            // Create a new chart
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels, // Days of the week as labels
                    datasets: [{
                        label: 'Calories',
                        data: calories, // Total calories as data
                        backgroundColor: '#ff6b6b',
                        borderRadius: 8, // Rounded corners on bars
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: 'Calories',
                            }
                        },
                        x: {
                            title: {
                                display: true,
                                text: 'Day of the Week',
                            }
                        }
                    }
                }
            });
        })
        .catch(error => console.error("Error fetching weekly calories:", error));

});