CREATE TABLE IF NOT EXISTS submissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    activity_id INT NOT NULL,
    student_id INT NOT NULL,
    submission_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    content TEXT,
    grade VARCHAR(10),
    feedback TEXT,
    FOREIGN KEY (activity_id) REFERENCES activities(id),
    FOREIGN KEY (student_id) REFERENCES users(id)
);
