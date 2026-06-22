// Global variables
let authToken = localStorage.getItem('bankToken');
let currentUser = localStorage.getItem('bankUser');

// Service URLs
const AUTH_URL = 'http://localhost:8085/api/auth';
const ACCOUNT_URL = 'http://localhost:8082/api/accounts';
const TRANSACTION_URL = 'http://localhost:8083/api/transactions';
const PAYMENT_URL = 'http://localhost:8084/api/payments';

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    if (authToken && currentUser) {
        showDashboard();
        loadUserData();
    } else {
        showAuth();
    }
});

// Helper: Show toast notification
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Helper: Make API requests
async function apiRequest(url, method, data, requiresAuth = true) {
    const headers = {
        'Content-Type': 'application/json'
    };
    
    if (requiresAuth && authToken) {
        headers['Authorization'] = `Bearer ${authToken}`;
    }
    
    const options = {
        method: method,
        headers: headers
    };
    
    if (data) {
        options.body = JSON.stringify(data);
    }
    
    try {
        const response = await fetch(url, options);
        const result = await response.json();
        
        if (!response.ok) {
            throw new Error(result.message || result.error || 'Request failed');
        }
        
        return result;
    } catch (error) {
        showToast(error.message, 'error');
        throw error;
    }
}

// Auth functions
function showAuth() {
    document.getElementById('authSection').style.display = 'flex';
    document.getElementById('dashboardSection').style.display = 'none';
    document.getElementById('transferSection').style.display = 'none';
    document.getElementById('paymentsSection').style.display = 'none';
    document.getElementById('historySection').style.display = 'none';
    document.getElementById('navLinks').style.display = 'none';
    document.getElementById('userInfo').style.display = 'none';
}

function showDashboard() {
    if (!authToken) return showAuth();
    
    document.getElementById('authSection').style.display = 'none';
    document.getElementById('dashboardSection').style.display = 'block';
    document.getElementById('transferSection').style.display = 'none';
    document.getElementById('paymentsSection').style.display = 'none';
    document.getElementById('historySection').style.display = 'none';
    document.getElementById('navLinks').style.display = 'flex';
    document.getElementById('userInfo').style.display = 'flex';
    
    document.getElementById('welcomeName').textContent = currentUser;
    document.getElementById('usernameDisplay').textContent = currentUser;
    
    loadAccounts();
}

function showTransfer() {
    if (!authToken) return showAuth();
    
    document.getElementById('dashboardSection').style.display = 'none';
    document.getElementById('transferSection').style.display = 'block';
    document.getElementById('paymentsSection').style.display = 'none';
    document.getElementById('historySection').style.display = 'none';
    
    loadAccountsForSelect('fromAccount');
}

function showPayments() {
    if (!authToken) return showAuth();
    
    document.getElementById('dashboardSection').style.display = 'none';
    document.getElementById('transferSection').style.display = 'none';
    document.getElementById('paymentsSection').style.display = 'block';
    document.getElementById('historySection').style.display = 'none';
    
    loadAccountsForSelect('payFromAccount');
}

function showHistory() {
    if (!authToken) return showAuth();
    
    document.getElementById('dashboardSection').style.display = 'none';
    document.getElementById('transferSection').style.display = 'none';
    document.getElementById('paymentsSection').style.display = 'none';
    document.getElementById('historySection').style.display = 'block';
    
    loadTransactionHistory();
}

function switchTab(tab) {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const btns = document.querySelectorAll('.tab-btn');
    
    if (tab === 'login') {
        loginForm.classList.add('active');
        registerForm.classList.remove('active');
        btns[0].classList.add('active');
        btns[1].classList.remove('active');
    } else {
        loginForm.classList.remove('active');
        registerForm.classList.add('active');
        btns[0].classList.remove('active');
        btns[1].classList.add('active');
    }
}

async function login(event) {
    event.preventDefault();
    
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    try {
        const result = await apiRequest(`${AUTH_URL}/login`, 'POST', {
            username: username,
            password: password
        }, false);
        
        authToken = result.token;
        currentUser = result.username;
        localStorage.setItem('bankToken', authToken);
        localStorage.setItem('bankUser', currentUser);
        
        showToast('Login successful!', 'success');
        showDashboard();
        
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function register(event) {
    event.preventDefault();
    
    const fullName = document.getElementById('regFullName').value;
    const email = document.getElementById('regEmail').value;
    const username = document.getElementById('regUsername').value;
    const password = document.getElementById('regPassword').value;
    
    try {
        const result = await apiRequest(`${AUTH_URL}/register`, 'POST', {
            fullName: fullName,
            email: email,
            username: username,
            password: password
        }, false);
        
        authToken = result.token;
        currentUser = result.username;
        localStorage.setItem('bankToken', authToken);
        localStorage.setItem('bankUser', currentUser);
        
        showToast('Registration successful!', 'success');
        showDashboard();
        
        // Create a default savings account for new user
        setTimeout(() => {
            createDefaultAccount();
        }, 1000);
        
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function createDefaultAccount() {
    try {
        await apiRequest(ACCOUNT_URL, 'POST', {
            username: currentUser,
            accountType: 'SAVINGS',
            initialDeposit: 1000
        });
        
        loadAccounts();
        showToast('Default savings account created with $1000!', 'success');
    } catch (error) {
        console.error('Failed to create default account:', error);
    }
}

async function createAccount() {
    const accountType = document.getElementById('newAccountType').value;
    
    if (!accountType) {
        showToast('Please select account type', 'error');
        return;
    }
    
    try {
        await apiRequest(ACCOUNT_URL, 'POST', {
            username: currentUser,
            accountType: accountType,
            initialDeposit: 0
        });
        
        showToast('Account created successfully!', 'success');
        loadAccounts();
        
        document.getElementById('newAccountType').value = '';
        
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function loadAccounts() {
    try {
        const accounts = await apiRequest(`${ACCOUNT_URL}/user/${currentUser}`, 'GET');
        
        const accountsList = document.getElementById('accountsList');
        accountsList.innerHTML = '';
        
        let totalBalance = 0;
        
        accounts.forEach(account => {
            totalBalance += account.balance;
            
            const accountDiv = document.createElement('div');
            accountDiv.className = 'account-item';
            accountDiv.innerHTML = `
                <div class="account-info">
                    <h4>${account.accountType} Account</h4>
                    <div class="account-number">${account.accountNumber}</div>
                </div>
                <div class="account-balance">$${account.balance.toFixed(2)} ${account.currency}</div>
            `;
            accountsList.appendChild(accountDiv);
        });
        
        // Add total balance
        const totalDiv = document.createElement('div');
        totalDiv.className = 'account-item';
        totalDiv.style.background = '#e8f0fe';
        totalDiv.innerHTML = `
            <div class="account-info">
                <h4>Total Balance</h4>
            </div>
            <div class="account-balance" style="color: #667eea;">$${totalBalance.toFixed(2)}</div>
        `;
        accountsList.appendChild(totalDiv);
        
        return accounts;
        
    } catch (error) {
        console.error('Failed to load accounts:', error);
        return [];
    }
}

async function loadAccountsForSelect(selectId) {
    try {
        const accounts = await apiRequest(`${ACCOUNT_URL}/user/${currentUser}`, 'GET');
        const select = document.getElementById(selectId);
        
        select.innerHTML = '<option value="">Select Account</option>';
        
        accounts.forEach(account => {
            const option = document.createElement('option');
            option.value = account.accountNumber;
            option.textContent = `${account.accountType} - ${account.accountNumber} ($${account.balance.toFixed(2)})`;
            select.appendChild(option);
        });
        
    } catch (error) {
        console.error('Failed to load accounts for select:', error);
    }
}

async function loadUserData() {
    await loadAccounts();
}

async function transferFunds(event) {
    event.preventDefault();
    
    const fromAccount = document.getElementById('fromAccount').value;
    const toAccount = document.getElementById('toAccount').value;
    const amount = parseFloat(document.getElementById('amount').value);
    const description = document.getElementById('description').value;
    
    if (!fromAccount || !toAccount || !amount) {
        showToast('Please fill all required fields', 'error');
        return;
    }
    
    if (amount <= 0) {
        showToast('Amount must be greater than 0', 'error');
        return;
    }
    
    try {
        const result = await apiRequest(`${TRANSACTION_URL}/transfer`, 'POST', {
            fromAccount: fromAccount,
            toAccount: toAccount,
            amount: amount,
            description: description || 'Fund transfer',
            username: currentUser
        });
        
        showToast(result.message || 'Transfer successful!', 'success');
        
        // Reset form
        document.getElementById('toAccount').value = '';
        document.getElementById('amount').value = '';
        document.getElementById('description').value = '';
        
        // Refresh data
        loadAccounts();
        loadAccountsForSelect('fromAccount');
        
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function payBill(event) {
    event.preventDefault();
    
    const fromAccount = document.getElementById('payFromAccount').value;
    const billType = document.getElementById('billType').value;
    const billerName = document.getElementById('billerName').value;
    const amount = parseFloat(document.getElementById('billAmount').value);
    
    if (!fromAccount || !billType || !billerName || !amount) {
        showToast('Please fill all fields', 'error');
        return;
    }
    
    if (amount <= 0) {
        showToast('Amount must be greater than 0', 'error');
        return;
    }
    
    try {
        const result = await apiRequest(`${PAYMENT_URL}/pay-bill`, 'POST', {
            fromAccount: fromAccount,
            billType: billType,
            billerName: billerName,
            amount: amount,
            username: currentUser,
            description: `${billType} bill payment`
        });
        
        showToast(result.message || 'Bill paid successfully!', 'success');
        
        // Reset form
        document.getElementById('billerName').value = '';
        document.getElementById('billAmount').value = '';
        
        // Refresh data
        loadAccounts();
        loadAccountsForSelect('payFromAccount');
        
    } catch (error) {
        showToast(error.message, 'error');
    }
}

async function loadTransactionHistory() {
    try {
        const transactions = await apiRequest(`${TRANSACTION_URL}/history/${currentUser}`, 'GET');
        
        const historyDiv = document.getElementById('transactionHistory');
        
        if (!transactions || transactions.length === 0) {
            historyDiv.innerHTML = '<div class="loading">No transactions yet</div>';
            return;
        }
        
        historyDiv.innerHTML = '';
        
        transactions.forEach(txn => {
            const txnDiv = document.createElement('div');
            txnDiv.className = 'history-item';
            
            txnDiv.innerHTML = `
                <div>
                    <strong>${txn.description || 'Transaction'}</strong>
                    <br>
                    <small>From: ${txn.fromAccount}</small>
                    <br>
                    <small>To: ${txn.toAccount}</small>
                    <br>
                    <small>${new Date(txn.timestamp).toLocaleString()}</small>
                    <br>
                    <small style="color: ${txn.status === 'COMPLETED' ? '#28a745' : '#dc3545'}">${txn.status}</small>
                </div>
                <div style="font-weight: bold; color: #667eea;">
                    $${txn.amount.toFixed(2)}
                </div>
            `;
            historyDiv.appendChild(txnDiv);
        });
        
    } catch (error) {
        console.error('Failed to load transaction history:', error);
        document.getElementById('transactionHistory').innerHTML = 
            '<div class="loading">Failed to load history</div>';
    }
}

function logout() {
    localStorage.removeItem('bankToken');
    localStorage.removeItem('bankUser');
    authToken = null;
    currentUser = null;
    showAuth();
    showToast('Logged out successfully', 'success');
}