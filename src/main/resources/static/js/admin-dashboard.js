document.addEventListener("DOMContentLoaded", () => {
    // Tab Switching Functionality
    const setupTabSwitching = () => {
        document.querySelectorAll(".tab-link").forEach(tab => {
            tab.addEventListener("click", (e) => {
                e.preventDefault();

                // Remove active class from all tabs and contents
                document.querySelectorAll(".tab-link").forEach(link => link.classList.remove("active"));
                document.querySelectorAll(".tab-content").forEach(content => content.classList.remove("active"));

                // Activate the clicked tab and corresponding content
                const target = tab.getAttribute("data-target");
                tab.classList.add("active");
                document.getElementById(target).classList.add("active");
            });
        });
    };

    // Modal Toggling
    window.toggleModal = (modalId) => {
        const modal = document.getElementById(modalId);
        modal.style.display = modal.style.display === "flex" ? "none" : "flex";
    };

    // Fetch and Display Statistics
    const fetchStatistics = () => {
        fetch("/admin/statistics")
            .then(response => response.json())
            .then(data => {
                const statistics = data[0];

                // Update the statistics cards
                document.getElementById("entriesLastWeek").textContent = statistics.entriesCount;
                document.getElementById("averageCaloriesPerUser").textContent = statistics.averageCalories.toFixed(2);
                document.getElementById("usersExceedingLimit").textContent = statistics.usersExceededSpendingLimit;

                // Populate the exceeding users table
                const exceedingUsersTableBody = document.getElementById("exceedingUsersTableBody");
                exceedingUsersTableBody.innerHTML = "";
                statistics.exceedingUsers.forEach(user => {
                    const newRow = `
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.name}</td>
                            <td>${user.email}</td>
                            <td>${user.totalSpendings.toFixed(2)}</td>
                        </tr>
                    `;
                    exceedingUsersTableBody.insertAdjacentHTML("beforeend", newRow);
                });
            })
            .catch(error => console.error("Error fetching statistics:", error));
    };

    // Fetch and Display Users
    const fetchUsers = () => {
        fetch("/admin/users")
            .then(response => response.json())
            .then(data => {
                const usersTableBody = document.getElementById("usersTableBody");
                usersTableBody.innerHTML = ""; // Clear previous rows
                data.forEach(user => {
                    const newRow = `
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.firstName} ${user.lastName}</td>
                            <td>${user.username}</td>
                            <td>${user.email}</td>
                            <td>${user.roleName}</td>
                            <td>${new Date(user.createdAt).toLocaleDateString()}</td>
                            <td>${new Date(user.updatedAt).toLocaleDateString()}</td>
                            <td>
                                <button class="btn btn-primary btn-sm" onclick="editUser(${user.id})">Edit</button>
                                <button class="btn btn-danger btn-sm" onclick="deleteUser(${user.id})">Delete</button>
                            </td>
                        </tr>
                    `;
                    usersTableBody.insertAdjacentHTML("beforeend", newRow);
                });
            })
            .catch(error => console.error("Error fetching users:", error));
    };

    // Fetch and Display Food Entries
    const fetchFoodEntries = () => {
        fetch("/admin/food-entries")
            .then(response => response.json())
            .then(data => {
                const foodEntriesTableBody = document.getElementById("foodEntriesTableBody");
                foodEntriesTableBody.innerHTML = ""; // Clear previous rows
                data.forEach(entry => {
                    const newRow = `
                        <tr>
                            <td>${entry.id}</td>
                            <td>${entry.userId}</td>
                            <td>${entry.name}</td>
                            <td>${entry.calories}</td>
                            <td>${entry.price.toFixed(2)}</td>
                            <td>${new Date(entry.createdAt).toLocaleDateString()}</td>
                            <td>
                                <button class="btn btn-primary btn-sm" onclick="editFoodEntry(${entry.id})">Edit</button>
                                <button class="btn btn-danger btn-sm" onclick="deleteFoodEntry(${entry.id})">Delete</button>
                            </td>
                        </tr>
                    `;
                    foodEntriesTableBody.insertAdjacentHTML("beforeend", newRow);
                });
            })
            .catch(error => console.error("Error fetching food entries:", error));
    };

    // Handle Form Submission for Adding a Food Entry
    document.getElementById("addFoodEntryForm").addEventListener("submit", (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);
        const foodEntryData = Object.fromEntries(formData);

        fetch("/admin/food-entries", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(foodEntryData),
        })
            .then(() => {
                fetchFoodEntries();
                toggleModal("addFoodEntryModal");
            })
            .catch(console.error);
    });

    // Handle Form Submission for Editing a Food Entry
    document.getElementById("editFoodEntryForm").addEventListener("submit", (event) => {
        event.preventDefault();
        const formData = new FormData(event.target);
        const foodEntryId = formData.get("id");
        const updatedData = Object.fromEntries(formData);

        fetch(`/admin/food-entries/${foodEntryId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updatedData),
        })
            .then(() => {
                fetchFoodEntries();
                toggleModal("editFoodEntryModal");
            })
            .catch(console.error);
    });

    // Initial Setup
    setupTabSwitching();
    fetchStatistics();
    fetchUsers();
    fetchFoodEntries();
});

// Edit Food Entry (Move this function to the global scope)
function editFoodEntry(entryId) {
    fetch(`/admin/food-entries/${entryId}`)

        .then(response => response.json())
        .then(data => {
            document.getElementById("editEntryId").value = data.id;
            document.getElementById("editFoodName").value = data.name;
            document.getElementById("editCalories").value = data.calories;
            document.getElementById("editPrice").value = data.price;

            toggleModal("editFoodEntryModal");
        })
        .catch(console.error);
}

// Fetch and Display Users
function fetchUsers() {
    fetch("/admin/users")
        .then(response => response.json())
        .then(data => {
            const usersTableBody = document.getElementById("usersTableBody");
            usersTableBody.innerHTML = ""; // Clear previous rows
            data.forEach(user => {
                const newRow = `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.firstName} ${user.lastName}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>${user.roleName}</td>
                        <td>${new Date(user.createdAt).toLocaleDateString()}</td>
                        <td>${new Date(user.updatedAt).toLocaleDateString()}</td>
                        <td>
                            <button class="btn btn-primary btn-sm" onclick="editUser(${user.id})">Edit</button>
                            <button class="btn btn-danger btn-sm" onclick="deleteUser(${user.id})">Delete</button>
                        </td>
                    </tr>
                `;
                usersTableBody.insertAdjacentHTML("beforeend", newRow);
            });
        })
        .catch(error => console.error("Error fetching users:", error));
}

// Fetch and Display Food Entries
function fetchFoodEntries() {
    fetch("/admin/food-entries")
        .then(response => response.json())
        .then(data => {
            const foodEntriesTableBody = document.getElementById("foodEntriesTableBody");
            foodEntriesTableBody.innerHTML = ""; // Clear previous rows
            data.forEach(entry => {
                const newRow = `
                    <tr>
                        <td>${entry.id}</td>
                        <td>${entry.userId}</td>
                        <td>${entry.name}</td>
                        <td>${entry.calories}</td>
                        <td>${entry.price.toFixed(2)}</td>
                        <td>${new Date(entry.createdAt).toLocaleDateString()}</td>
                        <td>
                            <button class="btn btn-primary btn-sm" onclick="editFoodEntry(${entry.id})">Edit</button>
                            <button class="btn btn-danger btn-sm" onclick="deleteFoodEntry(${entry.id})">Delete</button>
                        </td>
                    </tr>
                `;
                foodEntriesTableBody.insertAdjacentHTML("beforeend", newRow);
            });
        })
        .catch(error => console.error("Error fetching food entries:", error));
}

// Delete Food Entry
function deleteFoodEntry(entryId) {
    if (confirm("Are you sure you want to delete this entry?")) {
        fetch(`/admin/food-entries/${entryId}`, { method: "DELETE" })
            .then(() => fetchFoodEntries()) // Call the global fetchFoodEntries
            .catch(console.error);
    }
}

// Edit User Function
function editUser(userId) {
    fetch(`/admin/users/${userId}`)
        .then(response => response.json())
        .then(data => {
            // Populate the modal with the existing user data
            document.getElementById("editUserId").value = data.id;
            document.getElementById("editFirstName").value = data.firstName;
            document.getElementById("editLastName").value = data.lastName;
            document.getElementById("editUsername").value = data.username;
            document.getElementById("editEmail").value = data.email;
            document.getElementById("editRole").value = data.roleId;

            // Show the modal
            toggleModal("editUserModal");
        })
        .catch(console.error);
}

// Handle Form Submission for Editing User
document.getElementById("editUserForm").addEventListener("submit", (event) => {
    event.preventDefault();

    const formData = new FormData(event.target);
    const userId = formData.get("id");
    const updatedData = Object.fromEntries(formData); // Convert form data to JSON object

    fetch(`/admin/users/${userId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updatedData),
    })
        .then(() => {
            fetchUsers();
            toggleModal("editUserModal");
        })
        .catch(console.error);
});


// Delete User
function deleteUser(userId) {
    if (confirm("Are you sure you want to delete this user?")) {
        fetch(`/admin/users/${userId}`, { method: "DELETE" })
            .then(() => fetchUsers()) // Call the global fetchUsers
            .catch(console.error);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    // Tab Switching Functionality
    document.querySelectorAll(".tab-link").forEach(tab => {
        tab.addEventListener("click", (e) => {
            e.preventDefault();

            // Remove active class from all tabs and contents
            document.querySelectorAll(".tab-link").forEach(link => link.classList.remove("active"));
            document.querySelectorAll(".tab-content").forEach(content => content.classList.remove("active"));

            // Activate the clicked tab and corresponding content
            const target = tab.getAttribute("data-target");
            tab.classList.add("active");
            document.getElementById(target).classList.add("active");
        });
    });

    // Modal Toggling
    window.toggleModal = (modalId) => {
        const modal = document.getElementById(modalId);
        modal.style.display = modal.style.display === "flex" ? "none" : "flex";
    };

    // Initial Fetch Calls
    fetchUsers();
    fetchFoodEntries();
});



