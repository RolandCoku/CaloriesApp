document.addEventListener("DOMContentLoaded", () => {
    const showWeeklySummaryModal = () => {
        fetch("/food-entries/user/weekly-summary")
            .then(response => response.json())
            .then(data => {
                data = data[0];
                document.getElementById("daysAboveThreshold").textContent = data.daysOverCalorieLimit;
                document.getElementById("totalSpendings").textContent = data.totalSpendings;
                document.getElementById("totalCaloriesConsumed").textContent = data.totalCaloriesConsumed;
                new bootstrap.Modal(document.getElementById("weeklySummaryModal")).show();
            })
            .catch(error => console.error("Error fetching weekly summary:", error));
    };

    // Show the modal when the user logs in
    showWeeklySummaryModal();

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

    // Notification banner
    const showNotification = (message, type) => {
        const banner = document.createElement("div");
        banner.className = `notification-banner ${type}`;
        banner.textContent = message;
        document.body.appendChild(banner);

        setTimeout(() => {
            banner.classList.add("show");
        }, 100);

        setTimeout(() => {
            banner.classList.remove("show");
            setTimeout(() => {
                banner.remove();
                checkPermanentBanner();
            }, 500);
        }, 3000);
    };

    const showPermanentBanner = (message, type) => {
        const banner = document.getElementById("permanentBanner");
        banner.className = `permanent-banner ${type}`;
        banner.textContent = message;
        banner.style.display = "block";
    };

    const checkPermanentBanner = () => {
        const today = new Date().toISOString().split("T")[0];
        let caloriesExceeded = false;
        let spendingExceeded = false;

        fetch(`/food-entries/user/total-calories/date/${today}`)
            .then(response => response.json())
            .then(calories => {
                if (calories > 2500) {
                    caloriesExceeded = true;
                }
                return fetch(`/food-entries/user/total-spending/date/${today}`);
            })
            .then(response => response.json())
            .then(spending => {
                if (spending > 1000) {
                    spendingExceeded = true;
                }

                if (caloriesExceeded && spendingExceeded) {
                    showPermanentBanner("You have exceeded both your daily calorie and spending limits!", "both");
                } else if (caloriesExceeded) {
                    showPermanentBanner("You have exceeded your daily calorie limit!", "calorie");
                } else if (spendingExceeded) {
                    showPermanentBanner("You have surpassed your spending limit!", "spending");
                }
            })
            .catch(error => console.error("Error fetching data:", error));
    };

    const checkLimits = () => {
        const today = new Date().toISOString().split("T")[0];

        fetch(`/food-entries/user/total-calories/date/${today}`)
            .then(response => response.json())
            .then(calories => {
                if (calories > 2500) {
                    showNotification("You have exceeded your daily calorie limit!", "calorie");
                }
            })
            .catch(error => console.error("Error fetching total calories:", error));

        fetch(`/food-entries/user/total-spending/date/${today}`)
            .then(response => response.json())
            .then(spending => {
                if (spending > 1000) {
                    showNotification("You have surpassed your spending limit!", "spending");
                }
            })
            .catch(error => console.error("Error fetching total spending:", error));
    };

    checkLimits();
    // Update the weekly calorie and weekly spending totals
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
                    labels: labels,
                    datasets: [{
                        label: 'Calories',
                        data: calories,
                        backgroundColor: '#ff6b6b',
                        borderRadius: 8,
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

    fetch("/food-entries/user/weekly-spending")
        .then(response => response.json())
        .then(data => {
            const labels = data.map(entry => entry.day);
            const spending = data.map(entry => entry.totalSpending);

            const ctxSpending = document.getElementById('spendingChart').getContext('2d');
            new Chart(ctxSpending, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Spending (€)',
                        data: spending,
                        backgroundColor: '#4caf50',
                        borderRadius: 8,
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
                                text: 'Spending (€)',
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
        .catch(error => console.error("Error fetching weekly spending:", error));
});