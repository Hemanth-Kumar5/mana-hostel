import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import AdminDashboard from './pages/AdminDashboard';
import AdminComplaints from './pages/AdminComplaints';
import AdminCashPayments from './pages/AdminCashPayments';
import StudentDashboard from './pages/StudentDashboard';
import StudentFeeManagement from './pages/StudentFeeManagement';
import StudentComplaint from './pages/StudentComplaint';
import StudentMessMenu from './pages/StudentMessMenu';
import FeeStatus from './pages/FeeStatus';
import StudentPaymentHistory from './pages/StudentPaymentHistory';
import StudentRoomDetails from './pages/StudentRoomDetails';
import AdminRooms from './pages/AdminRooms';
import './App.css';

function App() {
  return (
    <Router>
      <div className="app">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/auth/login" element={<Login />} />
          <Route path="/login" element={<Login />} />
          <Route path="/auth/register" element={<Register />} />
          <Route path="/register" element={<Register />} />
          <Route path="/admin/admin-dashboard" element={<AdminDashboard />} />
          <Route path="/admin/rooms" element={<AdminRooms />} />
          <Route path="/admin/complaints" element={<AdminComplaints />} />
          <Route path="/admin/payment-collection" element={<AdminCashPayments />} />
          <Route path="/student/student-dashboard" element={<StudentDashboard />} />
          <Route path="/student/room-details" element={<StudentRoomDetails />} />
          <Route path="/student/room-access" element={<StudentRoomDetails />} />
          <Route path="/room-details" element={<StudentRoomDetails />} />
          <Route path="/room-access" element={<StudentRoomDetails />} />
          <Route path="/student/fee-management" element={<StudentFeeManagement />} />
          <Route path="/student/raise-complaint" element={<StudentComplaint />} />
          <Route path="/student/mess-menu" element={<StudentMessMenu />} />
          <Route path="/admin/students-fee-status" element={<FeeStatus />} />
          <Route path="/student/fee-history" element={<StudentPaymentHistory />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
