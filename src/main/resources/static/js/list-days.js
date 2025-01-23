document.addEventListener("DOMContentLoaded", () => {
    fetch("/food-entries/user/days-over-limit")
        .then(response => response.json())
        .then(data => {
            const tableBody = document.getElementById("overLimitTableBody");
            tableBody.innerHTML = ""; // Clear existing rows
            data.forEach(entry => {
                const newRow = `
                    <tr>
                        <td>${new Date(entry.date).toLocaleDateString()}</td>
                        <td>${entry.day}</td>
                        <td>${entry.totalCalories}</td>
                        <td>${entry.totalSpending.toFixed(2)}</td>
                    </tr>
                `;
                tableBody.insertAdjacentHTML("beforeend", newRow);
            });
        })
        .catch(error => console.error("Error fetching over calorie limit days:", error));
});